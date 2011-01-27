package net.dirtyfilthy.bitcoin.protocol;

public class GetAddressPacket extends Packet {

	public GetAddressPacket() {
		super();
		command="getaddr";
	}

	public GetAddressPacket(int ver) {
		super(ver,"getaddr");
	}

}
