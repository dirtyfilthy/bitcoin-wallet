package net.dirtyfilthy.bitcoin.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.protocol.Packet;
import net.dirtyfilthy.bitcoin.util.HashTools;


public class Tx  implements ByteArrayable {
	private long transactionVersion;
	private long lockTime;
	private Vector<TxIn> txInputs=new Vector<TxIn>();
	private Vector<TxOut> txOutputs=new Vector<TxOut>();
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
		int index=0;
		for(TxIn in : t.txInputs){
			this.txInputs.add(in.clone());
		}
		for(TxOut out : t.txOutputs){
			this.txOutputs.add(out.clone());
		}
		
	}
	
	public String toString(){
		String s="transactionVersion "+this.transactionVersion+"\n";
		s+="lockTime "+this.lockTime+"\n";
		s+="INPUTS\n";
		for(TxIn in : txInputs ){
			s+=in.toString();
			s+="\n";
		}
		s+="OUTPUTS\n";
		for(TxOut out : txOutputs){
			s+=out.toString();
			s+="\n";
		}
		return s;
		
	}
	
	public static boolean verifySignature(Tx from, Tx to, int index, int hashType){
		if(index>=to.getTxInputs().size()){
			System.out.println("Input index greater than length");
			return false;
		}
		TxIn input=to.getTxInputs().get(index);
		if (input.getOutpointIndex() >= from.getTxOutputs().size()){
			System.out.println("Output index greater than length");
			return false;
		}
		TxOut output=from.getTxOutputs().get((int) input.getOutpointIndex());
		if(!Script.verifyScript(input.getScript(), output.getScript(), to, index, hashType)){
			System.out.println("Verify script failed");
			return false;
		}
		return true;
	}
	
	public Tx(DataInputStream in) throws IOException {
		int items;
		this.transactionVersion=((long) Integer.reverseBytes(in.readInt())) & 0xffffffff;
		items=(int) Packet.readUnsignedVarInt(in);
		for(int i=0;i<items;i++){
			this.txInputs.add(new TxIn(in));
		}
		items=(int) Packet.readUnsignedVarInt(in);
		for(int i=0;i<items;i++){
			this.txOutputs.add(new TxOut(in));
		}
		this.lockTime=((long) Integer.reverseBytes(in.readInt())) & 0xffffffff;
	}
	
	public byte[] toByteArray() {
		ByteBuffer dataBuffer=ByteBuffer.allocate(500000);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		dataBuffer.putInt((int) this.transactionVersion);
		dataBuffer.put(Packet.createUnsignedVarInt(this.txInputs.size()));
		for(TxIn in : txInputs){
			dataBuffer.put(in.toByteArray());
		}
		dataBuffer.put(Packet.createUnsignedVarInt(this.txOutputs.size()));
		for(TxOut out : txOutputs){
			dataBuffer.put(out.toByteArray());
		}
		dataBuffer.putInt((int) this.lockTime);
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

	public void setTxInputs(Vector<TxIn> txInputs) {
		this.txInputs = txInputs;
	}

	public Vector<TxIn> getTxInputs() {
		return txInputs;
	}

	public void setTxOutputs(Vector<TxOut> txOutputs) {
		this.txOutputs = txOutputs;
	}

	public Vector<TxOut> getTxOutputs() {
		return txOutputs;
	}
	
	public byte[] hash(){
		return HashTools.doubleSha256(this.toByteArray());
	}

}
