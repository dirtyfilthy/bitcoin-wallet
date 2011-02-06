package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Block;

public class HeadersPacket extends Packet {
	
	Vector<Block> headers=new Vector<Block>();
	
	public HeadersPacket(long ver) {
		super(ver, "headers");
	}
	
	public HeadersPacket() {
		super();
		setCommand("headers");
	}
	
	public byte[] create(){
		writeUnsignedVarInt(headers.size());
		for(Block b : headers){
			dataBuffer.put(b.toByteArray());
		}
		return toByteArray();
	}
	
	public void readData(DataInputStream in) throws IOException{
		headers.clear();
		int items=(int) readUnsignedVarInt(in);
		System.out.println("header items :"+items);
		for(int i=0;i<items;i++){
			
			// SER_BLOCKHEADERONLY is defined in original but only used for disk access
			// we need to get the (always zero) txCount so we pass in includeTransactions=true
			
			headers.add(new Block(in,true));
		}
	}
	
	public Vector<Block> headers(){
		return this.headers;
	}


}
