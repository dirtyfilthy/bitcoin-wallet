package net.dirtyfilthy.bitcoin.core;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.protocol.Packet;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.BigIntegerTools;
import net.dirtyfilthy.bitcoin.util.QuickHash;

public class Block implements ByteArrayable {
	private long blockVersion;
	private byte[] previousHash=new byte[32];
	private byte[] merkleRoot=new byte[32];
	private java.util.Date timestamp;
	private long bits;
	private long nonce;
	private byte hash[];
	private Vector<Tx> transactions=new Vector<Tx>();
	private boolean headersOnly=false;
	private BigInteger bigIntegerHash;
	private BigInteger previousBigIntegerHash;
	private int height;
	private BigInteger totalWork;
	static final private BigInteger MAX_WORK=BigInteger.valueOf(1).shiftLeft(256);
	
	
	public Block(){
		this.blockVersion=1;
	}
	
	public BigInteger bigIntegerHash(){
		if(bigIntegerHash==null){
			bigIntegerHash=new BigInteger(this.hash());
		}
		return bigIntegerHash;
	}
	
	public BigInteger previousBigIntegerHash(){
		if(previousBigIntegerHash==null){
			previousBigIntegerHash=new BigInteger(this.previousHash);
			
		}
		return previousBigIntegerHash;
	}
	
	public Block(DataInputStream in, boolean includeTransactions) throws IOException{
		this.blockVersion=((long) Integer.reverseBytes(in.readInt())) & 0xffffffffL;
		in.readFully(previousHash);
		in.readFully(merkleRoot);
		this.timestamp=new java.util.Date((((long) Integer.reverseBytes(in.readInt())) & 0xffffffffL)*1000);
		this.bits=((long) Integer.reverseBytes(in.readInt())) & 0xffffffffL;
		this.nonce=((long) Integer.reverseBytes(in.readInt())) & 0xffffffffL;
		
		transactions=new Vector<Tx>();
		if(includeTransactions){
			int items=(int) Packet.readUnsignedVarInt(in);
			for(int i=0;i<items;i++){
				this.transactions.add(new Tx(in));
			}
		}
		else{
			headersOnly=true;
		}
	}
	
	public long getTime(){
		return this.timestamp.getTime();
	}
	
	public BigInteger getWork(){
		BigInteger work=MAX_WORK.divide(this.targetHash().add(BigInteger.ONE));
		return work;
	}
	
	public BigInteger targetHash(){
		return BigIntegerTools.uncompactBigInt(bits);
	}
	
	public byte[] toByteArray(){
		return toByteArray(true);
	}
	
	public byte[] toByteArray(boolean includeTransactions){
		try{
		ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
		DataOutputStream dataOut=new DataOutputStream(byteOut);
		dataOut.writeInt(Integer.reverseBytes((int) this.blockVersion));
		dataOut.write(this.previousHash);
		dataOut.write(this.merkleRoot);
		dataOut.writeInt(Integer.reverseBytes((int) (this.timestamp.getTime()/1000)));
		dataOut.writeInt(Integer.reverseBytes((int) this.bits));
		dataOut.writeInt(Integer.reverseBytes((int) this.nonce));
		if(includeTransactions){
			dataOut.write(Packet.createUnsignedVarInt(transactions.size()));
			for(Tx tx : transactions){
				dataOut.write(tx.toByteArray());
			}
		}
	
		return byteOut.toByteArray();
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public byte[] hash(){
		if(this.hash!=null){
			return this.hash;
		}
		this.hash=QuickHash.doubleSha256(this.toByteArray(false));
		return hash;
	}
	
	public boolean validProofOfWork() {
		byte[] h=QuickHash.reverseByteArray(this.hash());
		if(h[0]!=0){
			return false;
		}
		return (new BigInteger(h).compareTo(this.targetHash())<=0);
	}
	
	
	public void setBlockVersion(long blockVersion) {
		this.blockVersion = blockVersion;
	}
	public long getBlockVersion() {
		return blockVersion;
	}
	public void setMerkleRoot(byte[] merkleRoot) {
		this.merkleRoot = merkleRoot;
	}
	public byte[] getMerkleRoot() {
		return merkleRoot;
	}
	public void setTimestamp(java.util.Date timestamp) {
		this.timestamp = timestamp;
	}
	public java.util.Date getTimestamp() {
		return timestamp;
	}
	public void setBits(long bits) {
		this.bits = bits;
	}
	public long getBits() {
		return bits;
	}
	public void setNonce(long nonce) {
		this.nonce = nonce;
	}
	public long getNonce() {
		return nonce;
	}


	public void setPreviousHash(byte[] previousHash) {
		this.previousHash = previousHash;
	}


	public byte[] getPreviousHash() {
		return previousHash;
	}

	public void setTransactions(Vector<Tx> transactions) {
		this.transactions = transactions;
	}

	public Vector<Tx> getTransactions() {
		return transactions;
	}
	
	public void addTransaction(Tx tx){
		transactions.add(tx);
		
	}

	public void setHeadersOnly(boolean headersOnly) {
		this.headersOnly = headersOnly;
	}

	public boolean isHeadersOnly() {
		return headersOnly;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setTotalWork(BigInteger totalWork) {
		this.totalWork = totalWork;
	}

	public BigInteger getTotalWork() {
		return totalWork;
	}
	
	

}
