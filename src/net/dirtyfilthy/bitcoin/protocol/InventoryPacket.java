package net.dirtyfilthy.bitcoin.protocol;

import java.io.IOException;
import java.io.DataInputStream;

public class InventoryPacket extends Packet {

	private InventoryVector[] inventoryVectors=new InventoryVector[0];
	
	public InventoryPacket() {
		super();
		command="inv";
	}

	public InventoryPacket(int ver) {
		super(ver,"inv");
	}

	public void setInventoryVectors(InventoryVector[] inventoryVectors) {
		this.inventoryVectors = inventoryVectors;
	}

	public InventoryVector[] getInventoryVectors() {
		return inventoryVectors;
	}
	
	public byte[] create(){
		writeUnsignedVarInt(inventoryVectors.length);
		for(InventoryVector inv : inventoryVectors){
			dataBuffer.put(inv.toByteArray());
		}
		return toByteArray();
	}
	
	public void readData(DataInputStream in) throws IOException{
		int items=(int) readUnsignedVarInt(in);
		inventoryVectors=new InventoryVector[items];
		for(int i=0;i<items;i++){
			inventoryVectors[i]=new InventoryVector(in);
		}
	}

}
