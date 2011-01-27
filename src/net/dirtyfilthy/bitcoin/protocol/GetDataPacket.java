package net.dirtyfilthy.bitcoin.protocol;

public class GetDataPacket extends InventoryPacket {

	public GetDataPacket() {
		super();
		command="getdata";
	}

	public GetDataPacket(int ver) {
		super(ver);
		command="getdata";
		// TODO Auto-generated constructor stub
	}

}
