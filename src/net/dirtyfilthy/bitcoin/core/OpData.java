package net.dirtyfilthy.bitcoin.core;

// this class is a total waste of time but java doesn't have a stinking Pair!

public class OpData {

	private byte[] data;
	private int code;
	public OpData(int code, byte[] data) {
		this.code=code;
		this.data=data;
	}
	
	public int code(){
		return this.code;
	}
	
	public byte[] data(){
		return this.data;
	}
	
	public OpCode opCode(){
		return OpCode.getByCode(this.code);
	}

}
