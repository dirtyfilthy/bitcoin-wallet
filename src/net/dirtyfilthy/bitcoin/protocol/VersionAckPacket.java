package net.dirtyfilthy.bitcoin.protocol;

public class VersionAckPacket extends Packet {
	
	public VersionAckPacket(){
		super();
		command="verack";	
	}

	public VersionAckPacket(long ver){
		super();
		command="verack";
		version=ver;
	}
}
