package net.dirtyfilthy.bitcoin.core;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class Address implements ByteArrayable {
	private long services;
	private InetAddress ip;
	private int port;
	private ByteBuffer dataBuffer;
	private int LENGTH=26;
	private Date lastSeen=new Date(100000000000L);
	private Date lastTry=new Date(0);
	private Date lastConnected=new Date(0);
	public Address(){
		dataBuffer=ByteBuffer.allocate(LENGTH);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		setServices(1);
	}
	
	public String toString(){
		return ip.toString()+":"+port;
	}
	
	public Address(InetAddress ip, int port){
		dataBuffer=ByteBuffer.allocate(LENGTH);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		setServices(1);
		setIp(ip);
		setPort(port);
	}
	
	public Address(String ip, int port) throws UnknownHostException{
		setServices(1);
		setIp(InetAddress.getByName(ip));
		setPort(port);
	}
	
	public Address(DataInputStream in, boolean timestamp) throws IOException {
		if(timestamp){
			lastSeen=new Date(((long) Integer.reverseBytes(in.readInt()) & 0xffffffffL)*1000);
		}
		services=Long.reverseBytes(in.readLong());
		byte[] ip=new byte[4];
		in.skip(12); // reserved
		in.read(ip);
		setIp(InetAddress.getByAddress(ip));
		port=(int) Short.reverseBytes(in.readShort()) & 0xff;
		
	}

	public void setServices(long services) {
		this.services = services;
	}

	public long getServices() {
		return services;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}
	
	
	public byte[] toByteArray(boolean timestamp){
		byte[] reserved={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		byte[] addr;
		if(timestamp){
			dataBuffer=ByteBuffer.allocate(LENGTH+4);
			dataBuffer.putInt((int) (lastSeen.getTime() * .001));
		}
		else{
			dataBuffer=ByteBuffer.allocate(LENGTH);
		}
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		dataBuffer.rewind();
		dataBuffer.putLong(services);
		dataBuffer.put(reserved);
		dataBuffer.put(ip.getAddress());
		dataBuffer.putShort((short) port);
		addr=dataBuffer.array();
		return addr;
	}

	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}

	public Date getLastSeen() {
		return lastSeen;
	}
	
	public int hashCode() {
		return ip.hashCode() ^ port;
	}
	
	public boolean equals(Address a){
		if(a==null){
			return false;
		}
		return this.getIp().equals(a.getIp()) && this.getPort()==a.getPort();
	}

	public void setLastTry(Date lastTry) {
		this.lastTry = lastTry;
	}

	public Date getLastTry() {
		return lastTry;
	}

	public void setLastConnected(Date lastConnected) {
		this.lastConnected = lastConnected;
	}

	public Date getLastConnected() {
		return lastConnected;
	}

	public byte[] toByteArray() {
		return toByteArray(false);
	}
	
	
}
