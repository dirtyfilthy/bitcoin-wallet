package net.dirtyfilthy.bitcoin.protocol;

import java.math.BigInteger;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.core.BtcValue;
import net.dirtyfilthy.bitcoin.core.OpCode;
import net.dirtyfilthy.bitcoin.core.Tx;
import net.dirtyfilthy.bitcoin.core.TxIn;
import net.dirtyfilthy.bitcoin.core.TxOut;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;

public class ProtocolVersion {
	public static final byte MAGIC[]={(byte) 0xf9, (byte) 0xbe,(byte) 0xb4,(byte) 0xd9};
	public static final byte TEST_MAGIC[]={(byte) 0xfa, (byte) 0xbf, (byte) 0xb5, (byte) 0xda }; 
	private static BigInteger proofOfWorkLimit=new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",16).shiftRight(32);
	static private boolean useTestNet=false;
	static private final OpCode[] DISABLED_OPCODES={OpCode.OP_SUBSTR,OpCode.OP_LEFT, OpCode.OP_RIGHT, OpCode.OP_INVERT, OpCode.OP_AND, OpCode.OP_OR, 
		OpCode.OP_XOR, OpCode.OP_2MUL, OpCode.OP_2DIV, OpCode.OP_MUL, OpCode.OP_DIV, OpCode.OP_MOD, OpCode.OP_LSHIFT,OpCode.OP_RSHIFT};

	static public OpCode[] disabledOpCodes(){
		return DISABLED_OPCODES;
		
	}
	
	static public int version(){
		return 31800;
	}
	
	static public BigInteger proofOfWorkLimit(){
		return proofOfWorkLimit;
	}
	
	static public Block genesisBlock(){
		Block g=new Block();
		g.setDifficulty(0x1d00ffff);
		g.setTimestamp(new java.util.Date(1231006505000L));
		g.setBlockVersion(1);
		g.setMerkleRoot(Hex.decode("0x4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"));
		TxIn in=new TxIn();
		in.script().pushData(486604799);
		in.script().pushData(4);
		in.script().pushData(Hex.decode("0x736B6E616220726F662074756F6C69616220646E6F63657320666F206B6E697262206E6F20726F6C6C65636E61684320393030322F6E614A2F33302073656D695420656854"));
		TxOut out=new TxOut();
		out.setValue(new BtcValue(50*BtcValue.COIN));
		out.script().pushData(Hex.decode("0x5F1DF16B2B704C8A578D0BBAF74D385CDE12C11EE50455F3C438EF4C3FBCF649B6DE611FEAE06279A60939E028A8D65C10B73071A6F16719274855FEB0FD8A6704"));
		out.script().pushOp(OpCode.OP_CHECKSIG);
		Tx tx=new Tx();
		tx.getTxInputs().add(in);
		tx.getTxOutputs().add(out);
		g.addTransaction(tx);
		return g;
	}
	
	static public byte[] magic(){
		if(useTestNet){
			return TEST_MAGIC;
		}
		else{
			return MAGIC;
		}
	}
	
	static public int ircPort(){
		return 6667;
	}
	
	static public byte addressVersion(){
		return (byte) (useTestNet ? 0xff : 0x00);
	}
	
	static public String ircHost(){
		return "92.243.23.21";
	}
	
	static public long targetTimespan(){ // in seconds
		return  14 * 24 * 60 * 60;
	}
	
	static public long targetInterval(){ // in seconds
		return  10 * 60;
	}
	
	static public int medianTimeSpan(){
		return 11;
	}
	

	
	static public String ircChannel(){
		return (useTestNet ? "#bitcoinTEST" : "#bitcoin");
	}
	
	static public boolean isUsingTestNet(){
		return useTestNet;
	}
	
	static public void useTestNet(boolean u){
		useTestNet=u;
	}
	
	
	
	
	

}
