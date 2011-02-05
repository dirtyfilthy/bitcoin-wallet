package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Address;
import net.dirtyfilthy.bitcoin.util.MyHex;

public class GetBlocksPacket extends Packet {
	
	private Vector<byte[]>startHashes=new Vector<byte[]>();
	private byte[] endHash=new byte[32];
	
	public GetBlocksPacket() {
		super();
		command="getblocks";
	}

	public GetBlocksPacket(long ver) {
		super(ver);
		command="getblocks";
	}
	
	public void readData(DataInputStream in) throws IOException{
		this.version=(long) Integer.reverseBytes(in.readInt()) & 0xffffffffL;
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
		System.out.println("putting hashes");
		dataBuffer.putInt((int) this.version);
		writeUnsignedVarInt(startHashes.size());
		for(byte[] hash : startHashes){
			System.out.println("Sending hash "+MyHex.encode(hash));
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
