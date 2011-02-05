package net.dirtyfilthy.bitcoin.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BtcValue {
	
	byte[] bytes=new byte[8];
	public static final int COIN = 100000000;
	private BigInteger value=BigInteger.ZERO;
	
	
	
	public BtcValue(DataInputStream in) throws IOException{
		ByteBuffer b=ByteBuffer.allocate(9);
		b.put(0, (byte) 0); // force this to be positive
		b.putLong(Long.reverseBytes(in.readLong()));
		value=new BigInteger(b.array());
	}
	
	public BtcValue(long v){
		ByteBuffer b=ByteBuffer.allocate(9);
		b.put(0, (byte) 0); // force this to be positive
		b.putLong(v);
		value=new BigInteger(b.array());
	}
	
	BtcValue(){
	}
	
	public byte[] toByteArray(){
		ByteBuffer b=ByteBuffer.allocate(8);
		b.putLong(Long.reverseBytes(value.longValue()));
		return b.array();
	}

}
