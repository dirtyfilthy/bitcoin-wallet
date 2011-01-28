package net.dirtyfilthy.bitcoin.core;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.protocol.Packet;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.KeyTools;
import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bitcoin.util.QuickHash;
import net.dirtyfilthy.bouncycastle.jce.ECNamedCurveTable;
import net.dirtyfilthy.bouncycastle.jce.ECPointUtil;

import net.dirtyfilthy.bouncycastle.jce.provider.asymmetric.ec.EC5Util;
import net.dirtyfilthy.bouncycastle.jce.spec.ECNamedCurveParameterSpec;


import net.dirtyfilthy.bouncycastle.math.ec.ECCurve;
import net.dirtyfilthy.bouncycastle.math.ec.ECPoint;


public class Script  implements ByteArrayable {

	private Vector<OpData> scriptCode=new Vector<OpData>();; 
	private byte[] rawBytes=new byte[0];
	private final static byte[] SCRIPT_TRUE={1};
	private final static byte[] SCRIPT_FALSE={0};
	Stack<byte[]> stack;
	Stack<byte[]> altStack;
	
	public Script(byte[] rawBytes){
		stack=new Stack<byte[]>();
		altStack=new Stack<byte[]>();
		DataInputStream in2=new DataInputStream(new ByteArrayInputStream(rawBytes));
		OpData op;
		while(true){
			try {
				op=this.getOp(in2);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if(op==null){
				break;
			}
			scriptCode.add(op);
		}
	}

	public Script(){
		stack=new Stack<byte[]>();
		altStack=new Stack<byte[]>();
	}

	Script(Vector<OpData> code){
		stack=new Stack<byte[]>();
		altStack=new Stack<byte[]>();
		scriptCode=code;
	}


	Script(Script s){
		stack=new Stack<byte[]>();
		altStack=new Stack<byte[]>();
		scriptCode=(Vector<OpData>) s.scriptCode.clone();
	}
	
	
	public void setStack(Stack<byte[]> s){
		this.stack=s;
	}
	
	public Stack<byte[]> getStack(){
		return this.stack;
	}
	
	
	



	public static byte[] signatureHash(Script sub,Tx txTo,int txInIndex, int hashType){
		Script sub2=new Script(sub);
		byte[] fail={(byte) 1};
		Tx txTmp=new Tx(txTo);
		sub2.deleteOp(OpCode.OP_CODESEPARATOR);
		TxIn[] txInputs=txTmp.getTxInputs();
		if(txInIndex>=txInputs.length){
			return fail;
		}
		for(TxIn in : txInputs){
			in.setScript(new Script());
		}
		txInputs[txInIndex].setScript(sub2);
		if((hashType & 0x1f) == Tx.SIGHASH_NONE) 
		{ 
			// Wildcard payee 
			txTmp.setTxOutputs(new TxOut[0]); 

			// Let the others update at will 
			for (int i = 0; i < txInputs.length; i++) 
				if (i != txInIndex) 
					txInputs[i].setOutpointIndex(0);
		} 
		else if ((hashType & 0x1f) == Tx.SIGHASH_SINGLE) 
		{ 
			// Only lockin the txout payee at same index as txin 
			int out = txInIndex; 
			if (out >= txTmp.getTxOutputs().length) 
			{ 
				return fail; 
			} 
			TxOut[] txOutputs=txTmp.getTxOutputs();
			TxOut[] txOutputs2=new TxOut[txOutputs.length+1];
			System.arraycopy(txOutputs, 0, txOutputs2, 0, txOutputs.length);
			txOutputs=txOutputs2;
			txOutputs[txOutputs.length-1]=new TxOut();
			for (int i = 0; i < out; i++) 
				txOutputs[i].setNull(); 

			// Let the others update at will 
			for (int i = 0; i < txInputs.length; i++) 
				if (i != txInIndex) 
					txInputs[i].setOutpointIndex(0);
		}
		if ((hashType & Tx.SIGHASH_ANYONECANPAY)!=0)
		{
			TxIn tmp = txTmp.getTxInputs()[txInIndex];
			TxIn[] t=new TxIn[1];
			t[0]=tmp;
			txTmp.setTxInputs(t);
		}

		// Serialize and hash
		byte[] txBytes=txTmp.toByteArray();
		byte[] toHash=new byte[txBytes.length+4];
		System.arraycopy(txBytes, 0, toHash, 0, txBytes.length);
		ByteBuffer b=ByteBuffer.allocate(4);
		b.putInt(Integer.reverseBytes(hashType));
		System.arraycopy(b.array(), 0, toHash, txBytes.length, 4);
		return QuickHash.doubleSha256(toHash);
	}


	public static boolean checkSig(byte[] sig,byte[] pubKey,Script sub,Tx txTo,int txInIndex, int hashType) throws NoSuchAlgorithmException, NoSuchProviderException{
		ECPublicKey key=KeyTools.decodePublicKey(pubKey);
		if(sig.length==0){
			return false;
		}
		if(hashType==0){
			hashType=sig[sig.length-1];
		}
		else if(hashType!=sig[sig.length-1]){
			return false;
		}
		byte[] temp=new byte[sig.length-1];
		System.arraycopy(sig, 0, temp, 0, sig.length-1);
		sig=temp;
		byte[] signatureHash= signatureHash(sub, txTo, txInIndex, hashType);
		return KeyTools.verifySignedData(key, signatureHash,sig);
	}

	public Script subScript(int start, int end){
		return new Script((Vector<OpData>) scriptCode.subList(start, end));
	}



	Script(DataInputStream in) throws IOException{
		rawBytes=Packet.readVariableField(in);
		DataInputStream in2=new DataInputStream(new ByteArrayInputStream(rawBytes));
		OpData op;
		while(true){
			op=this.getOp(in2);
			if(op==null){
				break;
			}
			scriptCode.add(op);
		}
	}


	private OpData getOp(DataInputStream in) throws IOException{
		int opcode=in.read();
		
		if(opcode<0){
			return null;
		}
		opcode=opcode & 0xff;
		byte[] data=null;
		if(opcode<=OpCode.OP_PUSHDATA4.code()){
			int size=opcode;
			OpCode op=OpCode.getByCode(opcode);
			if(op!=null){
				switch(op){
				case OP_PUSHDATA1: 
					size=in.read();
					break;
				case OP_PUSHDATA2:
					size=(int) Short.reverseBytes(in.readShort()) & 0xffff;
					break;
				case OP_PUSHDATA4:
					size=(int) Integer.reverseBytes(in.readInt());
				}
			}
			data=new byte[size];
			in.read(data);
		}
		if(data==null){
			System.out.println(OpCode.getByCode(opcode).toString());
		}
		else{
			System.out.println(MyHex.encode(data));
		}
		return new OpData(opcode,data);

	}

	public void deleteData(byte[] d){
		if(d==null){
			return;
		}
		Iterator<OpData> i=scriptCode.iterator();
		while(i.hasNext()){
			OpData statement=i.next();
			if(Arrays.equals(d,statement.data())){
				i.remove();
			}
		}
	}

	public void deleteOp(OpCode op){
		if(op==null){
			return;
		}
		Iterator<OpData> i=scriptCode.iterator();
		while(i.hasNext()){
			OpData statement=i.next();
			if(statement.opCode().equals(op)){
				i.remove();
			}
		}
	}


	private boolean castToBool(BigInteger bn){
		if(bn.bitCount()==0){
			return false;
		}
		return true;
	}

	private boolean castToBool(byte[] b){
		BigInteger bn=new BigInteger(b);
		return castToBool(bn);
	}

	public boolean eval(Tx txTo, int txIn, int hashType){
		int opCount=0;
		Stack<Boolean> ifStack=new Stack<Boolean>();

		List<OpCode> disabledCodes=Arrays.asList(ProtocolVersion.disabledOpCodes());
		int index=0,separatorIndex=0;
		for(OpData statement : scriptCode){
			OpCode op=statement.opCode();
			int opcode=statement.code();
			boolean ifExec=!ifStack.contains(Boolean.FALSE);
			byte data[]=statement.data();
			if(opcode>OpCode.OP_16.code()){
				opCount++;
				if(opCount>201){
					return false;
				}	
			}

			if(data!=null && data.length>520){
				return false;
			}

			if(op!=null && disabledCodes.contains(op)){
				return false;
			}
			if(ifExec && data!=null){
				stack.push(data);
			}
			else if(ifExec ||  (OpCode.OP_IF.code() <= opcode && opcode <= OpCode.OP_ENDIF.code())){
				switch(op){ // start of switch block
				case OP_1NEGATE:
				case OP_1:
				case OP_2:
				case OP_3:
				case OP_4:
				case OP_5:
				case OP_6:
				case OP_7:
				case OP_8:
				case OP_9:
				case OP_10:
				case OP_11:
				case OP_12:
				case OP_13:
				case OP_14:
				case OP_15:
				case OP_16:
					stack.push(BigInteger.valueOf(opcode-OpCode.OP_1.code()+1).toByteArray());
					break;
				case OP_NOP:
					break;
				case OP_IF:
				case OP_NOTIF:
					// <expression> if [statements] [else [statements]] endif
					boolean ifValue = false;
					if (ifExec)
					{
						if (stack.empty()){
							return false;
						}
						byte[] v=stack.pop();
						ifValue = castToBool(v);
						if (op == OpCode.OP_NOTIF){
							ifValue = !ifValue;
						}
					}
					ifStack.push(new Boolean(ifValue));
					break;
				case OP_ELSE:
					if (ifStack.empty()){
						return false;
					}
					ifStack.push(Boolean.valueOf(!ifStack.pop().booleanValue()));
					break;
				case OP_ENDIF:
					if (ifStack.empty()){
						return false;
					}
					ifStack.pop();

				case OP_VERIFY:
					// (true -- ) or
					// (false -- false) and return
					if (stack.empty()){
						return false;
					}
					boolean ifValue2 = castToBool(stack.pop());
					if (ifValue2){
						break;
					}
					return false;

				case OP_RETURN:
					return false;

				case OP_TOALTSTACK:
					if (stack.empty()){
						return false;
					}
					altStack.push(stack.pop());
					break;
				case OP_FROMALTSTACK:
					if (altStack.empty()){
						return false;
					}
					stack.push(altStack.pop());
					break;
				case OP_2DROP:
					if (stack.size()<2){
						return false;
					}
					stack.pop();
					stack.pop();
					break;
				case OP_2DUP:
					if (stack.size()<2){
						return false;
					}
					byte[] f2=stack.get(stack.size()-2);
					byte[] f1=stack.get(stack.size()-1);

					stack.push(f2);
					stack.push(f1);
					break;
				case OP_3DUP:
					if (stack.size()<3){
						return false;
					}
					byte[] f3=stack.get(stack.size()-3);
					f2=stack.get(stack.size()-2);
					f1=stack.get(stack.size()-1);
					stack.push(f3);
					stack.push(f2);
					stack.push(f1);
					break;
				case OP_2OVER:
					if (stack.size()<4){
						return false;
					}
					f2=stack.get(stack.size()-4);
					f1=stack.get(stack.size()-3);
					stack.push(f2);
					stack.push(f1);
					break;
				case OP_2ROT:
					if (stack.size()<6){
						return false;
					}
					f2=stack.remove(stack.size()-6);
					f1=stack.remove(stack.size()-5);
					stack.push(f2);
					stack.push(f1);
					break;
				case OP_2SWAP:
					if (stack.size()<4){
						return false;
					}
					f2=stack.remove(stack.size()-4);
					f1=stack.remove(stack.size()-3);
					stack.push(f2);
					stack.push(f1);
					break;
				case OP_IFDUP:
					if (stack.size()<1){
						return false;
					}
					f1=stack.peek();
					if(castToBool(f1)){
						stack.push(f1);
					}
				case OP_DEPTH:
					stack.push(BigInteger.valueOf(stack.size()).toByteArray());
					break;
				case OP_DROP:
					stack.pop();
					break;
				case OP_DUP:
					if (stack.size()<1){
						return false;
					}
					f1=stack.peek();
					stack.push(f1);
					break;
				case OP_NIP:
					if (stack.size()<2){
						return false;
					}
					stack.remove(stack.size()-2);
					break;
				case OP_OVER:
					if (stack.size()<2){
						return false;
					}
					stack.push(stack.get(stack.size()-2));
					break;
				case OP_PICK:
				case OP_ROLL:
					if (stack.size()<2){
						return false;
					}
					stack.push(stack.get(stack.size()-2));
					int n=new BigInteger(stack.pop()).intValue();
					if(n<0 || n>stack.size()){
						return false;
					}
					f1=stack.get(stack.size()-n-1);
					if(op==OpCode.OP_ROLL){
						stack.remove(stack.size()-n-1);
					}
					stack.push(f1);
					break;
				case OP_ROT:
					if (stack.size()<3){
						return false;
					}
					f1=stack.pop();
					f2=stack.pop();
					f3=stack.pop();
					stack.push(f1);
					stack.push(f3);
					stack.push(f2);
					break;
				case OP_SWAP:
					if (stack.size()<2){
						return false;
					}
					f1=stack.pop();
					f2=stack.pop();
					stack.push(f1);
					stack.push(f2);
					break;
				case OP_TUCK:
					if (stack.size()<2){
						return false;
					}
					f1=stack.pop();
					f2=stack.pop();
					stack.push(f1);
					stack.push(f2);
					stack.push(f1);
					break;

					// splice ops
				case OP_CAT:
					if (stack.size()<2){
						return false;
					}
					f1=stack.pop();
					f2=stack.pop();
					int new_length=f1.length+f2.length;
					if(new_length>512){
						return false;
					}
					f3=new byte[new_length];
					System.arraycopy(f1, 0, f3, 0, f1.length);
					System.arraycopy(f2, 0, f3, f1.length, f2.length);
					stack.push(f3);
					break;
				case OP_SUBSTR:
					if (stack.size()<3){
						return false;
					}
					f1=stack.pop();				   
					f2=stack.pop();
					f3=stack.pop();
					int begin=new BigInteger(f2).intValue();
					int end=new BigInteger(f1).intValue()+begin;
					if(begin<0 || end<begin){
						return false;
					}
					if(begin>f3.length){
						begin=f3.length;
					}
					if(end>f3.length){
						end=f3.length;
					}
					new_length=end-begin;
					byte f4[]=new byte[new_length];
					System.arraycopy(f3, begin, f4, 0, new_length);
					stack.push(f4);
					break;                   
				case OP_LEFT:
				case OP_RIGHT:
					if (stack.size()<2){
						return false;
					}
					f1=stack.pop();				   
					f2=stack.pop();
					new_length=new BigInteger(f1).intValue();
					if(new_length<0){
						return false;
					}
					if(new_length>f2.length){
						new_length=f2.length;
					}
					f3=new byte[new_length];
					if(op==OpCode.OP_LEFT){
						System.arraycopy(f2, 0, f3, 0, new_length);
					}
					else{
						System.arraycopy(f2, f2.length-new_length, f3, 0, new_length);
					}
					stack.push(f3);
					break;
				case OP_SIZE:
					if (stack.size()<1){
						return false;
					}
					f1=stack.peek();
					stack.push(BigInteger.valueOf(f1.length).toByteArray());
					break;
					// bitwise logic
				case OP_INVERT:
					if (stack.size()<1){
						return false;
					}
					f1=stack.pop();
					BigInteger bn=new BigInteger(f1);
					stack.push(bn.not().toByteArray());
					break;
				case OP_AND:
				case OP_OR:
				case OP_XOR:
					if (stack.size()<2){
						return false;
					}
					f1=stack.pop();
					f2=stack.pop();
					BigInteger bn1=new BigInteger(f1);
					BigInteger bn2=new BigInteger(f2);
					BigInteger bn3;
					if(op==OpCode.OP_AND){
						bn3=bn1.and(bn2);
					}
					else if(op==OpCode.OP_OR){
						bn3=bn1.or(bn2);
					}
					else{
						bn3=bn1.xor(bn2);
					}
					stack.push(bn3.toByteArray());
					break;
				case OP_EQUAL:
				case OP_EQUALVERIFY:
					if (stack.size()<2){
						return false;
					}
					f1=stack.pop();
					f2=stack.pop();
					boolean equal=Arrays.equals(f1, f2);
					stack.push(equal ? SCRIPT_TRUE : SCRIPT_FALSE);
					if(op==OpCode.OP_EQUALVERIFY){
						stack.pop();
						if(!equal){
							return false;
						}
					}
					break;
					//
					// Numeric
					//
				case OP_1ADD:
				case OP_1SUB:
				case OP_2MUL:
				case OP_2DIV:
				case OP_NEGATE:
				case OP_ABS:
				case OP_NOT:
				case OP_0NOTEQUAL:
					// (in -- out)
					if (stack.size() < 1)
					{
						return false;
					}
					bn=new BigInteger(stack.pop());
					switch (op)
					{
					case OP_1ADD:       
						bn=bn.add(BigInteger.ONE); 
						break;
					case OP_1SUB:       
						bn=bn.subtract(BigInteger.ONE);
						break;
					case OP_2MUL:       
						bn=bn.shiftLeft(1);
						break;
					case OP_2DIV:
						bn=bn.shiftRight(1);
						break;
					case OP_NEGATE:     
						bn = bn.negate(); 
						break;
					case OP_ABS:    
						bn = bn.abs();
						break;
					case OP_NOT:        
						bn = (bn.compareTo(BigInteger.ZERO)==0) ? BigInteger.ONE : BigInteger.ZERO;
					case OP_0NOTEQUAL:  
						bn = (bn.compareTo(BigInteger.ZERO)!=0)  ? BigInteger.ONE : BigInteger.ZERO;
						break;
					}
					stack.push(bn.toByteArray());
					break;
				case OP_ADD:
				case OP_SUB:
				case OP_MUL:
				case OP_DIV:
				case OP_MOD:
				case OP_LSHIFT:
				case OP_RSHIFT:
				case OP_BOOLAND:
				case OP_BOOLOR:
				case OP_NUMEQUAL:
				case OP_NUMEQUALVERIFY:
				case OP_NUMNOTEQUAL:
				case OP_LESSTHAN:
				case OP_GREATERTHAN:
				case OP_LESSTHANOREQUAL:
				case OP_GREATERTHANOREQUAL:
				case OP_MIN:
				case OP_MAX:
					// (x1 x2 -- out)
					if (stack.size() < 2)
						return false;
					bn2 = new BigInteger(stack.pop());
					bn1 = new BigInteger(stack.pop());
					bn=BigInteger.ZERO;
					switch (op){
					case OP_ADD:
						bn = bn1.add(bn2);
						break;

					case OP_SUB:
						bn = bn1.subtract(bn2);
						break;
					case OP_MUL:
						bn = bn1.multiply(bn2);
						break;

					case OP_DIV:
						bn = bn1.divide(bn2);
						break;

					case OP_MOD:
						bn=bn1.mod(bn2);
						break;

					case OP_LSHIFT:
						if (bn2.compareTo(BigInteger.ZERO)<0 || bn2.compareTo(BigInteger.valueOf(2048))>0){
							return false;
						}
						bn = bn1.shiftLeft(bn2.intValue());
						break;

					case OP_RSHIFT:
						if (bn2.compareTo(BigInteger.ZERO)<0 || bn2.compareTo(BigInteger.valueOf(2048))>0){
							return false;
						}
						bn = bn1.shiftRight(bn2.intValue());
						break;

					case OP_BOOLAND:             bn = (castToBool(bn1) && castToBool(bn2)) ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_BOOLOR:              bn = (castToBool(bn1) || castToBool(bn2)) ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_NUMEQUAL:            bn = bn1.equals(bn2) ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_NUMEQUALVERIFY:      bn = bn1.equals(bn2) ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_NUMNOTEQUAL:         bn = !bn1.equals(bn2) ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_LESSTHAN:            bn = bn1.compareTo(bn2)<0 ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_GREATERTHAN:         bn = bn1.compareTo(bn2)>0 ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_LESSTHANOREQUAL:     bn = bn1.compareTo(bn2)<=0 ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_GREATERTHANOREQUAL:  bn = bn1.compareTo(bn2)>=0 ? BigInteger.ONE : BigInteger.ZERO; break;
					case OP_MIN:                 bn = bn1.compareTo(bn2)< 0 ? bn1 : bn2; break;
					case OP_MAX:                 bn = bn1.compareTo(bn2)> 0 ? bn1 : bn2; break;


					}
					stack.push(bn.toByteArray());
					if(op==OpCode.OP_NUMEQUALVERIFY && !castToBool(stack.pop())){
						return false;
					}
					break;
				case OP_WITHIN:

					// (x min max -- out)
					if (stack.size() < 3)
						return false;
					bn3 = new BigInteger(stack.pop());
					bn2 = new BigInteger(stack.pop());
					bn1 = new BigInteger(stack.pop());

					ifValue = (bn2.compareTo(bn1)<=0 && bn1.compareTo(bn3)<0);

					stack.push(ifValue ? SCRIPT_TRUE : SCRIPT_FALSE);

					break;

					//
					// Crypto
					//
				case OP_RIPEMD160:
				case OP_SHA1:
				case OP_SHA256:
				case OP_HASH160:
				case OP_HASH256:

					// (in -- hash)
					if (stack.size() < 1)
						return false;
					byte[] ret;
					byte[] toHash=stack.pop();
					if (op == OpCode.OP_RIPEMD160)
						ret=QuickHash.ripemd160(toHash);
					else if (op == OpCode.OP_SHA1)
						ret=QuickHash.sha1(toHash);
					else if (op == OpCode.OP_SHA256)
						ret=QuickHash.sha256(toHash);
					else if (op == OpCode.OP_HASH160)
					{
						ret=QuickHash.hash160(toHash);
					}
					else
					{
						ret=QuickHash.doubleSha256(toHash);
					}
					stack.push(ret);

					break;
				case OP_CODESEPARATOR:

					// Hash starts after the code separator
					separatorIndex=index;

					break;

				case OP_CHECKSIG:
				case OP_CHECKSIGVERIFY:
					// (sig pubkey -- bool)
					if (stack.size() < 2)
						return false;

					byte[] pubKey=stack.pop();
					byte[] sig=stack.pop();

					Script sub=this.subScript(separatorIndex, scriptCode.size()-1);

					// Drop the signature, since there's no way for a signature to sign itself
					sub.deleteData(sig);
					try {
						ifValue=checkSig(sig,pubKey,sub,txTo,txIn, hashType);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					} catch (NoSuchProviderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException(e);
					}
					if(op==OpCode.OP_CHECKSIGVERIFY){
						if(!ifValue){
							return false;
						}
					}else{
						stack.push(ifValue ? SCRIPT_TRUE : SCRIPT_FALSE);
					}

					break;

				case OP_CHECKMULTISIG:
				case OP_CHECKMULTISIGVERIFY:
					// ([sig ...] num_of_signatures [pubkey ...] num_of_pubkeys -- bool)


					if (stack.size() < 1)
						return false;
					Vector<byte[]> keys=new Vector<byte[]>();
					Vector<byte[]> sigs=new Vector<byte[]>();
					int keysCount = new BigInteger(stack.pop()).intValue();
					if (keysCount < 0 || keysCount > 20)
						return false;
					opCount += keysCount;
					if (opCount > 201)
						return false;

					if (stack.size() < keysCount+1)
						return false;
					for(int i=0;i<keysCount;i++){
						keys.add(stack.pop());
					}
					if (stack.size() < 1)
						return false;

					int sigsCount = new BigInteger(stack.pop()).intValue();
					if (sigsCount < 0 || sigsCount > keysCount || stack.size() < sigsCount)
						return false;
					for(int i=0;i<sigsCount;i++){
						sigs.add(stack.pop());
					}



					// Subset of script starting at the most recent codeseparator
					Script sub2=this.subScript(separatorIndex, scriptCode.size()-1);

					// Drop the signatures, since there's no way for a signature to sign itself
					for (int k = 0; k < sigsCount; k++)
					{
						sub2.deleteData(sigs.get(k));
					}
					int isig=0;
					int ikey=0;
					boolean success = true;
					while (success && sigsCount > 0)
					{

						sig=sigs.get(isig);
						pubKey=keys.get(ikey);


						// Check signature
						try {
							if (checkSig(sig, pubKey, sub2, txTo, txIn, hashType))
							{
								isig++;
								sigsCount--;
							}
						} catch (NoSuchAlgorithmException e) {
							throw new RuntimeException(e);
						} catch (NoSuchProviderException e) {
							throw new RuntimeException(e);
						}
						ikey++;
						keysCount--;

						// If there are more signatures left than keys left,
						// then too many signatures have failed
						if (sigsCount > keysCount)
							success = false;
					}
					stack.push(success ? SCRIPT_TRUE : SCRIPT_FALSE);
					if (op == OpCode.OP_CHECKMULTISIGVERIFY){
						stack.pop();
						if(!success){
							return false;
						}
					}
					break;
				default:
					return false;

				} // end of switch block
				 if (stack.size() + altStack.size() > 1000){
					 return false;
				 }
				
			}  // end of if block

			index++;

		}	// end of for(OpData statement : scriptCode)
		if(!ifStack.empty())
			return false;
		return true;
	}


	public byte[] compile(){
		ByteBuffer dataBuffer=ByteBuffer.allocate(10000);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for(OpData statement : scriptCode){
			OpCode op=statement.opCode();
			byte[] data=statement.data();
			dataBuffer.put((byte) statement.code());
			if(data!=null){
				if(op!=null){
					int size=data.length;

					switch(op){
					case OP_PUSHDATA1: 
						dataBuffer.put((byte) size);
						break;
					case OP_PUSHDATA2:
						dataBuffer.putShort((short) (size));
						break;
					case OP_PUSHDATA4:
						dataBuffer.putInt(size);
					}
				}
				dataBuffer.put(data);
			}
		}
		byte[] ret=new byte[dataBuffer.position()];
		ByteBuffer slicedBuffer=(ByteBuffer) dataBuffer.duplicate();
		slicedBuffer.rewind();
		slicedBuffer.limit(dataBuffer.position());
		slicedBuffer.get(ret);
		return ret;
	}
	
	public void pushOp(OpCode op){
		OpData statement=new OpData(op.code(),null);
		scriptCode.add(statement);		
	}
	
	public void pushData(byte[] data){
		int size=data.length;
		int opcode=0;
		if(size<OpCode.OP_PUSHDATA1.code()){
			opcode=size;
		}
		else if(size<0xff){
			opcode=OpCode.OP_PUSHDATA1.code();
		}
		else if(size<0xffff){
			opcode=OpCode.OP_PUSHDATA2.code();
		}
		else {
			opcode=OpCode.OP_PUSHDATA4.code();
		}
		OpData statement=new OpData(opcode,data);
		scriptCode.add(statement);
	}
	
	public byte[] getRawBytes(){
		return  compile();
	}

	public byte[] toByteArray() {
		byte[] raw=getRawBytes();
		byte[] varSize=Packet.createUnsignedVarInt(raw.length);
		ByteBuffer dataBuffer=ByteBuffer.allocate(varSize.length+raw.length);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		dataBuffer.put(varSize);
		dataBuffer.put(raw);
		return dataBuffer.array();
	}

	public void pushData(String string) {
		try {
			pushData(string.getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
	}
}
