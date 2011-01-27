package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import net.dirtyfilthy.bitcoin.core.Address;

public class VersionPacket extends Packet {
	
	private Date timeStamp;
	private String subversion;
	private long services;
	private long nonce;
	private long startingHeight;
	private Address remoteAddress;
	private Address fromAddress;
	
	public VersionPacket(int ver) {
		super(ver,"version");
		subversion="";
		services=1;
	}
	
	public VersionPacket() {
		super();
		subversion="";
		setCommand("version");
		services=1;
	}
	
	// no checksum on version
	
	//public byte[] checksum(){
	//	byte[] checksum={0,0,0,0};
	//	return checksum;
	//}
	
	public byte[] create(){
		dataBuffer.putInt(getVersion());
		dataBuffer.putLong(services);
		dataBuffer.putLong(getTimeStamp().getTime());
		dataBuffer.put(remoteAddress.toByteArray(false));
		if(version>=106){
			dataBuffer.put(fromAddress.toByteArray(false));
			dataBuffer.putLong(nonce);
			writeVariableStringField(subversion);
		}
		if(version>=209){
			dataBuffer.putInt((int) (startingHeight & 0xffffffff)); 
		}
		return toByteArray();
	}

	public void setRemoteAddress(Address remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public Address getRemoteAddress() {
		return remoteAddress;
	}

	public void setSubversion(String string) {
		this.subversion = string;
	}

	public String getSubversion() {
		return subversion;
	}

	public void setServices(long services) {
		this.services = services;
	}

	public long getServices() {
		return services;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
		
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = new Date(timeStamp);
		
	}

	
	public Date getTimeStamp() {
		if(timeStamp==null){
			timeStamp=Calendar.getInstance().getTime();
		}
		return timeStamp;
	}
	
	protected long readUnsignedInt(DataInputStream in) throws IOException{
		long ret=Integer.reverseBytes(in.readInt()) & 0xffffffff;
		return ret;
	}
	
	protected void readData(DataInputStream in) throws IOException {
		this.setVersion(Integer.reverseBytes(in.readInt()));
		System.out.println("version "+getVersion());
		this.setServices(Long.reverseBytes(in.readLong()));
		System.out.println("services "+getServices());
		this.setTimeStamp(in.readLong());
		this.setRemoteAddress(new Address(in,false));
		if(version>=106){
			this.setFromAddress(new Address(in,false));
			this.setNonce(Long.reverseBytes(in.readLong()));
			this.setSubversion(readVariableString(in));
		}
		if(version>=209){
			this.setStartingHeight(readUnsignedInt(in));
		}
	
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public long getNonce() {
		return nonce;
	}

	public void setFromAddress(Address fromAddress) {
		this.fromAddress = fromAddress;
	}

	public Address getFromAddress() {
		return fromAddress;
	}

	public void setStartingHeight(long startingHeight) {
		this.startingHeight = startingHeight;
	}

	public long getStartingHeight() {
		return startingHeight;
	}
	
}