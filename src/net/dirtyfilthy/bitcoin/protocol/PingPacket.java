package net.dirtyfilthy.bitcoin.protocol;

public class PingPacket extends Packet {
	
	public PingPacket() {
		super();
		command="ping";
	}

	public PingPacket(int ver) {
		super(ver,"ping");
	}


}
