package net.dirtyfilthy.bitcoin.protocol;

public class PingPacket extends Packet {
	
	public PingPacket() {
		super();
		command="ping";
	}

	public PingPacket(long ver) {
		super(ver,"ping");
	}


}
