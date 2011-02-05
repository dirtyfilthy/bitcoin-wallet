package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;

import net.dirtyfilthy.bitcoin.core.Tx;

public class TxPacket extends Packet {
	
	private Tx tx;

	public TxPacket(long ver) {
		super(ver,"tx");
	} 
	
	public TxPacket() {
		super();
		setCommand("tx");
	}
	
	public void readData(DataInputStream in) throws IOException{
		this.tx=new Tx(in);
	}
	
	public byte[] create(){
		return tx.toByteArray();
	}

	public void setTx(Tx tx) {
		this.tx = tx;
	}

	public Tx getTx() {
		return tx;
	}

	

}
