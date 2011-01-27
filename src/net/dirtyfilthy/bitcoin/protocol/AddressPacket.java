package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;

import net.dirtyfilthy.bitcoin.core.Address;

public class AddressPacket extends Packet {
	
	private Address[] addresses;
	
	public AddressPacket() {
		super();
		command="addr";
	}

	public AddressPacket(int ver) {
		super(ver,"addr");
	}

	public void setAddresses(Address[] addresses) {
		this.addresses = addresses;
	}

	public Address[] getAddresses() {
		return addresses;
	}
	
	public boolean timestamp(){
		return (version >= 31402);
	}
	
	public byte[] create(){
		writeUnsignedVarInt(addresses.length);
		for(Address addr : addresses){
			dataBuffer.put(addr.toByteArray(timestamp()));
		}
		return toByteArray();
	}
	
	public void readData(DataInputStream in) throws IOException{
		int items=(int) Packet.readUnsignedVarInt(in);
		this.addresses=new Address[items];
		for(int i=0;i<items;i++){
			this.addresses[i]=new Address(in,timestamp());
		}
	}
	
	

}
