package net.dirtyfilthy.bitcoin.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class TxOut  implements ByteArrayable, Cloneable  {
	private BtcValue value;
	private Script script;
	
	public TxOut(DataInputStream in) throws IOException{
		value=new BtcValue(in);
		script=new Script(in);	
	}
	
	public TxOut() {
		setNull(); 	
	}
	
	public void setNull(){
		this.value=new BtcValue(-1);
		this.script=new Script();
	}
	
	public String toString() {
		String s="Value: "+value.toLong()+"\n";
		s+="Script:\n"+script.toString()+"\n";
		return s;
	}
	
	public void setScript(Script script) {
		this.script = script;
	}
	public Script getScript() {
		return script;
	}
	
	public Script script() {
		return getScript();
	}
	
	public void setValue(BtcValue value) {
		this.value = value;
	}
	public BtcValue getValue() {
		return value;
	}
	
	public TxOut clone(){
		try {
			return (TxOut) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public byte[] toByteArray(){
		byte[] rawScript=this.script.toByteArray();
		ByteBuffer dataBuffer=ByteBuffer.allocate(rawScript.length+8);
		dataBuffer.put(value.toByteArray());
		dataBuffer.put(rawScript);
		return dataBuffer.array();
	}

}
