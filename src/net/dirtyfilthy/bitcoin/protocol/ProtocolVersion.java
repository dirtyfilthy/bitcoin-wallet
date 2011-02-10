package net.dirtyfilthy.bitcoin.protocol;

import java.math.BigInteger;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.core.BtcValue;
import net.dirtyfilthy.bitcoin.core.OpCode;
import net.dirtyfilthy.bitcoin.core.Tx;
import net.dirtyfilthy.bitcoin.core.TxIn;
import net.dirtyfilthy.bitcoin.core.TxOut;
import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bitcoin.util.QuickHash;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;

public class ProtocolVersion {
	public static final byte MAGIC[]={(byte) 0xf9, (byte) 0xbe,(byte) 0xb4,(byte) 0xd9};
	public static final byte TEST_MAGIC[]={(byte) 0xfa, (byte) 0xbf, (byte) 0xb5, (byte) 0xda }; 
	private static BigInteger proofOfWorkLimit=new BigInteger("00000000FFFF0000000000000000000000000000000000000000000000000000",16);
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
	
	// the genesis block, TODO: add testnet genesis block
	
	static public Block genesisBlock(){
		Block g=new Block();
		g.setHeight(0);
		g.setBits(486604799);
		g.setTimestamp(new java.util.Date(1231006505000L));
		g.setBlockVersion(1);
		g.setNonce(2083236893);
		g.setMerkleRoot(QuickHash.reverseByteArray(Hex.decode("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b")));
		TxIn in=new TxIn();
		in.script().pushData(486604799);
		in.script().pushData(4);
		in.script().pushData(Hex.decode("736B6E616220726F662074756F6C69616220646E6F63657320666F206B6E697262206E6F20726F6C6C65636E61684320393030322F6E614A2F33302073656D695420656854"));
		TxOut out=new TxOut();
		out.setValue(new BtcValue(50*BtcValue.COIN));
		out.script().pushData(Hex.decode("5F1DF16B2B704C8A578D0BBAF74D385CDE12C11EE50455F3C438EF4C3FBCF649B6DE611FEAE06279A60939E028A8D65C10B73071A6F16719274855FEB0FD8A6704"));
		out.script().pushOp(OpCode.OP_CHECKSIG);
		Tx tx=new Tx();
		tx.getTxInputs().add(in);
		tx.getTxOutputs().add(out);
		g.addTransaction(tx);
		return g;
	}
	
	// second block for testing

	static public Block secondBlock(){

		Block s=new Block();
		s.setBits(486604799);
		s.setTimestamp(new java.util.Date(1231469665000L));
		s.setBlockVersion(1);
		s.setNonce(2573394689L);
		s.setPreviousHash(QuickHash.reverseByteArray(Hex.decode("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f")));
		s.setMerkleRoot(QuickHash.reverseByteArray(Hex.decode("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098")));
		s.setHeadersOnly(true);
		// block header only
		return s;

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
