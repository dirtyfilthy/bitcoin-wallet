package net.dirtyfilthy.bitcoin.protocol;

import java.io.IOException;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.dirtyfilthy.bitcoin.core.ByteArrayable;

public class InventoryVector implements ByteArrayable {
	private int LENGTH=36;
	private byte[] hash=new byte[32];
	static public final int ERROR=0;
	static public final int MSG_TX=0;
	static public final int MSG_BLOCK=0;
	private int type=ERROR;
	private ByteBuffer dataBuffer;
	
	public InventoryVector(){
		dataBuffer=ByteBuffer.allocate(LENGTH);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public InventoryVector(DataInputStream in) throws IOException {
		dataBuffer=ByteBuffer.allocate(LENGTH);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		type=Integer.reverseBytes(in.readInt());
		in.read(hash);
	}
	
	public byte[] toByteArray(){
		dataBuffer.rewind();
		dataBuffer.putInt(type);
		dataBuffer.put(hash);
		return dataBuffer.array();
	}
	
	
	public void setHash(byte[] hash) {
		this.hash = hash;
	}
	public byte[] getHash() {
		return hash;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
	
	

}
