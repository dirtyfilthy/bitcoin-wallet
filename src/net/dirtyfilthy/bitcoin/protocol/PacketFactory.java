package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketFactory {
	
	private int version;
	private int packetType=0;

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}
	
	PacketFactory(){
		this.version=ProtocolVersion.version();
	}
	
	PacketFactory(int version){ 
		this.version=version;
		
	}
	
	public Packet readPacket(DataInputStream in) throws IOException {
		Packet dummy,real;;
		dummy=new Packet(version);
		
		// keep reading until we have a valid header
		
		while(true){
			in.mark(Packet.HEADER_LENGTH);
			try{
				dummy.readHeader(in);
			}
			catch(MalformedPacketException e){
				in.reset();
				in.skipBytes(1);
				continue;
			}
			break;
		}
		real=this.create(dummy.packetType());
		in.reset();
		real.readExternal(in);
		return real;
	}
	
	public Packet create(PacketType type){
		Packet packet;
		switch(type) {
		case ADDR: packet=new AddressPacket(this.version); break;
		case VERSION: packet=new VersionPacket(this.version); break;
		case VERACK: packet=new VersionAckPacket(this.version); break;
		case INV: packet=new InventoryPacket(this.version); break;
		case PING: packet=new PingPacket(this.version); break;
		case REPLY: packet=new ReplyPacket(this.version); break;
		case GETDATA: packet=new GetDataPacket(this.version); break;
		case GETADDR: packet=new GetAddressPacket(this.version); break;
		case BLOCK: packet=new BlockPacket(this.version); break;
		case TX: packet=new TxPacket(this.version); break;
		case GETBLOCKS: packet=new GetBlocksPacket(this.version); break;
		case GETHEADERS: packet=new GetHeadersPacket(this.version); break;
		case HEADERS: packet=new HeadersPacket(this.version); break;
		default: throw new RuntimeException("Invalid packet type");
		}
		packet.setPacketType(packetType);
		return packet;
	}

	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	public int getPacketType() {
		return packetType;
	}

}
