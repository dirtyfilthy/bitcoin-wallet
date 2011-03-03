package net.dirtyfilthy.bitcoin.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BtcValue {
	
	byte[] bytes=new byte[8];
	public static final long COIN = 100000000L;
	private long value=0;
	
	public long toLong(){
		return value;
	}
	
	public BtcValue(DataInputStream in) throws IOException{
		value=Long.reverseBytes(in.readLong());
	}
	
	public BtcValue(long v){
		value=v;
	}
	
	public BtcValue(double f){
		f=f*100*(COIN/100);
		f=(f > 0 ? f + 0.5 : f - 0.5);
		value=(long) (f);
	}
	
	public double toDouble(){
		return (double) value / (double) COIN;
	}
	
	public BtcValue(){
		value=0;
	}
	
	public byte[] toByteArray(){
		ByteBuffer b=ByteBuffer.allocate(8);
		b.putLong(Long.reverseBytes(value));
		return b.array();
	}

}

