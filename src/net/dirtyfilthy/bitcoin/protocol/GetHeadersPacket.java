package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;

public class GetHeadersPacket extends GetBlocksPacket {

	
	
	public GetHeadersPacket() {
		super();
		setCommand("getheaders");
	}

	public GetHeadersPacket(int ver) {
		super(ver);
		setCommand("getheaders");
		// TODO Auto-generated constructor stub
	}
	
	
}
