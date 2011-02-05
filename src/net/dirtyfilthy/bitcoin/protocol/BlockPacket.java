package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;

import net.dirtyfilthy.bitcoin.core.Block;

public class BlockPacket extends Packet {
	
	private Block block; 
	
	public BlockPacket(long ver) {
		super(ver,"block");
	}
	
	public BlockPacket() {
		super();
		setCommand("block");
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}
	
	public boolean includeTransactions(){
		return (packetType & (SER_GETHASH|SER_BLOCKHEADERONLY))!=0;
	}
	
	public void readData(DataInputStream in) throws IOException{
		this.block=new Block(in,includeTransactions());
	}
	
	public byte[] create(){
		return this.block.toByteArray(includeTransactions());
	}
	
	
	
	

}
