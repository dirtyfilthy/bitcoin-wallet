package net.dirtyfilthy.bitcoin.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.dirtyfilthy.bitcoin.protocol.Packet;

public class Tx  implements ByteArrayable {
	private long transactionVersion;
	private long lockTime;
	private TxIn[] txInputs;
	private TxOut[] txOutputs;
	public static final int SIGHASH_ALL = 1;
	public static final int SIGHASH_NONE = 2;
	public static final int SIGHASH_SINGLE = 3;
	public static final int SIGHASH_ANYONECANPAY = 0x80;
	
	public Tx(){
		this.transactionVersion=1;
		this.lockTime=0;
	}
	
	public Tx(Tx t){
		this.lockTime=t.lockTime;
		this.transactionVersion=t.transactionVersion;
		this.txInputs=new TxIn[t.txInputs.length];
		this.txOutputs=new TxOut[t.txOutputs.length];
		int index=0;
		for(TxIn in : t.txInputs){
			this.txInputs[index]=t.txInputs[index].clone();
			index++;
		}
		index=0;
		for(TxOut out : t.txOutputs){
			this.txOutputs[index]=t.txOutputs[index].clone();
			index++;
		}
	}
	
	public Tx(DataInputStream in) throws IOException {
		int items;
		this.transactionVersion=((long) Integer.reverseBytes(in.readInt())) & 0xffffffff;
		items=(int) Packet.readUnsignedVarInt(in);
		this.txInputs=new TxIn[items];
		for(int i=0;i<items;i++){
			this.txInputs[i]=new TxIn(in);
		}
		items=(int) Packet.readUnsignedVarInt(in);
		this.txOutputs=new TxOut[items];
		for(int i=0;i<items;i++){
			this.txOutputs[i]=new TxOut(in);
		}
		this.transactionVersion=((long) Integer.reverseBytes(in.readInt())) & 0xffffffff;
	}
	
	public byte[] toByteArray() {
		ByteBuffer dataBuffer=ByteBuffer.allocate(500000);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		dataBuffer.putInt((int) this.transactionVersion);
		dataBuffer.put(Packet.createUnsignedVarInt(this.txInputs.length));
		for(TxIn in : txInputs){
			dataBuffer.put(in.toByteArray());
		}
		dataBuffer.put(Packet.createUnsignedVarInt(this.txOutputs.length));
		for(TxOut out : txOutputs){
			dataBuffer.put(out.toByteArray());
		}
		byte[] dataContents=new byte[dataBuffer.position()];
		ByteBuffer slicedBuffer=(ByteBuffer) dataBuffer.duplicate();
		slicedBuffer.rewind();
		slicedBuffer.limit(dataBuffer.position());
		slicedBuffer.get(dataContents);
		return dataContents;
	}
	
	public void setTransactionVersion(long transactionVersion) {
		this.transactionVersion = transactionVersion;
	}

	public long getTransactionVersion() {
		return transactionVersion;
	}

	public void setLockTime(long lockTime) {
		this.lockTime = lockTime;
	}

	public long getLockTime() {
		return lockTime;
	}

	public void setTxInputs(TxIn[] txInputs) {
		this.txInputs = txInputs;
	}

	public TxIn[] getTxInputs() {
		return txInputs;
	}

	public void setTxOutputs(TxOut[] txOutputs) {
		this.txOutputs = txOutputs;
	}

	public TxOut[] getTxOutputs() {
		return txOutputs;
	}

}
