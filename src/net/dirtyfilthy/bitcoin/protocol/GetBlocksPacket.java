package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Address;

public class GetBlocksPacket extends Packet {
	
	private Vector<byte[]>startHashes;
	private byte[] endHash=new byte[32];
	
	public GetBlocksPacket() {
		super();
		command="getblocks";
	}

	public GetBlocksPacket(int ver) {
		super(ver);
		command="getblocks";
	}
	
	public void readData(DataInputStream in) throws IOException{
		int items=(int) Packet.readUnsignedVarInt(in);
		this.startHashes=new Vector<byte[]>();
		
		for(int i=0;i<items;i++){
			byte[] hash=new byte[32];
			in.read(hash);
			startHashes.add(hash);
		}
		in.read(endHash);
	}
	
	public byte[] create(){
		writeUnsignedVarInt(startHashes.size());
		for(byte[] hash : startHashes){
			dataBuffer.put(hash);
		}
		dataBuffer.put(endHash);
		return toByteArray();
	}

	public void setStartHashes(Vector<byte[]> startHashes) {
		this.startHashes = startHashes;
	}

	public Vector<byte[]> startHashes() {
		return startHashes;
	}

	public void setEndHash(byte[] endHash) {
		this.endHash = endHash;
	}

	public byte[] getEndHash() {
		return endHash;
	}

}
