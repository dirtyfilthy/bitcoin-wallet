package net.dirtyfilthy.bitcoin.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;


public class TxIn  implements ByteArrayable, Cloneable {
	private byte[] outpointHash=new byte[32];
	private long outpointIndex;
	private Script script;
	private long sequence;
	
	 
	public TxIn(DataInputStream in) throws IOException{
		in.read(outpointHash);
		System.out.println("outpoint: "+MyHex.encode(outpointHash));
		this.outpointIndex=((long) Integer.reverseBytes(in.readInt())) & 0xffffffff;
		System.out.println("index: "+outpointIndex);
		this.script=new Script(in);
		this.sequence=((long) Integer.reverseBytes(in.readInt())) & 0xffffffff;
	}
	
	public TxIn clone(){
		try {
			return (TxIn) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}


	public void setOutpointHash(byte[] outpointHash) {
		this.outpointHash = outpointHash;
	}


	public byte[] getOutpointHash() {
		return outpointHash;
	}


	public void setOutpointIndex(int outpointIndex) {
		this.outpointIndex = outpointIndex;
	}


	public long getOutpointIndex() {
		return outpointIndex;
	}


	public void setScript(Script script) {
		this.script = script;
	}


	public Script getScript() {
		return script;
	}


	public void setTransactionVersion(int transactionVersion) {
		this.sequence = transactionVersion;
	}


	public long getTransactionVersion() {
		return sequence;
	}
	
	public byte[] toByteArray(){
		byte[] rawScript=this.script.toByteArray();
		ByteBuffer dataBuffer=ByteBuffer.allocate(rawScript.length+40);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		dataBuffer.put(this.outpointHash);
		dataBuffer.putInt((int) this.outpointIndex);
		dataBuffer.put(rawScript);
		dataBuffer.putInt((int) this.sequence);
		return dataBuffer.array();
	}
	
	
	
	
}
