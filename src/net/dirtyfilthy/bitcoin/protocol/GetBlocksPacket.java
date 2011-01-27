package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;

import net.dirtyfilthy.bitcoin.core.Address;

public class GetBlocksPacket extends Packet {
	
	private byte[][] startHashes;
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
		this.startHashes=new byte[items][32];
		for(int i=0;i<items;i++){
			in.read(startHashes[i]);
		}
		in.read(endHash);
	}
	
	public byte[] create(){
		writeUnsignedVarInt(startHashes.length);
		for(byte[] hash : startHashes){
			dataBuffer.put(hash);
		}
		dataBuffer.put(endHash);
		return toByteArray();
	}

	public void setStartHashes(byte[][] startHashes) {
		this.startHashes = startHashes;
	}

	public byte[][] getStartHashes() {
		return startHashes;
	}

	public void setEndHash(byte[] endHash) {
		this.endHash = endHash;
	}

	public byte[] getEndHash() {
		return endHash;
	}

}
