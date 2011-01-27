package net.dirtyfilthy.bitcoin.protocol;

import java.math.BigInteger;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.OpCode;

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
