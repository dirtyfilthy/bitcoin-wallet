package net.dirtyfilthy.bitcoin.protocol;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Address;

public class ConnectionHandler {
	Vector<Connection> connections=new Vector<Connection>();
	AddressBook addressBook;
	Address localAddress;
	private int connectionsToMaintain=5;
	
	public ConnectionHandler() throws UnknownHostException{
		this.addressBook=new AddressBook();
		this.localAddress=new Address("127.0.0.1",18333);
	}
	
	public void maintainConnections(){
		int connectionsToAdd=connectionsToMaintain-connections.size();
		if(connectionsToAdd<=0){ 
			return; 
		}
		List<Address> toConnect;
		Vector<Address> possible=addressBook.getAddressConnectList();
		possible.removeAll(connections);
		if(possible.size()<connectionsToAdd){
			
				try {
					System.out.println("addresses: "+possible.size());
					bootstrap();
					possible=addressBook.getAddressConnectList();
					possible.removeAll(connections);
					System.out.println("addresses2: "+possible.size());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		toConnect=possible.subList(0, (connectionsToMaintain < possible.size() ? connectionsToMaintain : possible.size()));
		for(Address a : toConnect){
			try {
				connectTo(a);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		

	
	
	public synchronized Connection connectTo(Address a) throws IOException{
		addressBook.justTried(a);
		Connection c=new Connection(this,a);
		connections.add(c);
		addressBook.justConnected(a);
		VersionPacket p=(VersionPacket) c.createPacket(PacketType.VERSION);
		p.setRemoteAddress(c.getAddress());
		p.setFromAddress(localAddress);
		c.sendPacket(p);
		c.connect();
		return c;
	}
	
	public synchronized void handleConnectionError(Connection c, Exception e){
		e.printStackTrace();
		c.close();
		connections.remove(c);
	}
	
	public synchronized void bootstrap() throws UnknownHostException, IOException, InterruptedException{
		IrcBootStrap irc=new IrcBootStrap(ProtocolVersion.ircHost(),ProtocolVersion.ircPort(), ProtocolVersion.ircChannel());
		Vector<Address> addresses=irc.getAddresses();
		for(Address a : addresses){
			addressBook.justSeen(a);
		}
	}
	
	public synchronized void handlePacket(Connection c, Packet p){
		Address address=c.getAddress();
		System.out.println(p.toString());
		
		// silently ignore packets from a connection if we haven't received a version packet
		
		if(!c.hasReceivedVersion() && p.packetType()!=PacketType.VERSION){
			return;
		}
		
		switch(p.packetType()){
		case VERSION:
			VersionPacket v=(VersionPacket) p;
			int ourVersion=ProtocolVersion.version(), theirVersion=v.getVersion(), negotiatedVersion;
			negotiatedVersion=theirVersion<ourVersion ? theirVersion : ourVersion;
			c.setVersion(negotiatedVersion);
			c.hasReceivedVersion(true);
			addressBook.justSeen(address);
			if(negotiatedVersion>=209){
				Packet verack=c.createPacket(PacketType.VERACK);
				c.sendPacket(verack);
			}
			break;
		case VERACK:
			c.hasRecievedVerack(true);
			addressBook.justSeen(address);
			break;
		}
	}

	public void setConnectionsToMaintain(int maintainConnections) {
		this.connectionsToMaintain = maintainConnections;
	}

	public int getConnectionsToMaintain() {
		return this.connectionsToMaintain;
	}
	
	public int getConnectionsNumber() {
		return this.connections.size();
	}

}
