package net.dirtyfilthy.bitcoin.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BtcValue {
	
	byte[] bytes=new byte[8];
	public static final int COIN = 100000000;
	long value=0;
	
	
	
	BtcValue(DataInputStream in) throws IOException{
		value=Long.reverseBytes(in.readLong());
	}
	
	BtcValue(long v){
		value=v;
	}
	
	BtcValue(){
		value=0;
	}
	
	public byte[] toByteArray(){
		ByteBuffer b=ByteBuffer.allocate(8);
		b.putLong(Long.reverseBytes(value));
		return b.array();
	}

}
