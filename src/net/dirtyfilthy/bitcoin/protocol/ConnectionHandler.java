package net.dirtyfilthy.bitcoin.protocol;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Address;
import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.core.BlockChain;
import net.dirtyfilthy.bitcoin.core.InvalidBlockException;
import net.dirtyfilthy.bitcoin.core.OrphanBlockException;
import net.dirtyfilthy.bitcoin.util.MyHex;

public class ConnectionHandler {
	private Vector<Connection> connections=new Vector<Connection>();
	private AddressBook addressBook;
	private Address localAddress;
	private BlockChain blockChain;
	private int connectionsToMaintain=5;
	private MaintainConnectionsThread maintainThread;
	private boolean ircBootStrap=true;
	
	public ConnectionHandler() {
		this.addressBook=new AddressBook();
		try {
			this.localAddress=new Address("0.0.0.0",18333);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e); // should never happen
		}
		this.blockChain=new BlockChain();
		this.maintainThread=new MaintainConnectionsThread(this);
	
	}
	
	public void setIrcBootStrap(boolean boot){
		ircBootStrap=boot;
	}
	
	public void run(){
		this.maintainThread.start();
	}
	
	
	public synchronized void maintainConnections(){
		int connectionsToAdd=connectionsToMaintain-connections.size();
		if(connectionsToAdd<=0){ 
			return; 
		}
		List<Address> toConnect;
		Vector<Address> possible=addressBook.getAddressConnectList();
		
		removeConnectionsFromAddressList(connections,possible);
		
		if(possible.size()<connectionsToAdd && ircBootStrap){
			
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
	
	public void addAddress(Address a){
		addressBook.justSeen(a);
	}
		
	public synchronized void getInitialHeaders(){
		for(Connection c : connections){
			GetHeadersPacket gh=(GetHeadersPacket) c.createPacket(PacketType.GETHEADERS);
			gh.startHashes().add(blockChain.topBlock().hash());
			c.sendPacket(gh);
			
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
			long ourVersion=ProtocolVersion.version(), theirVersion=v.getVersion(), negotiatedVersion;
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
		case HEADERS:
			// primitive headers function
			HeadersPacket h=(HeadersPacket) p;
			if(h.headers().size()==0){
				break;
			}
			for(Block header : h.headers()){
				try {
					if(!blockChain.isKnown(header)){
						blockChain.addBlock(header);
					}
				} catch (InvalidBlockException e) {
					// TODO actually handle
					e.printStackTrace();
					continue;
				}
				catch (OrphanBlockException e) {
					// TODO actually handle
					e.printStackTrace();
					continue;
				}
			}
			GetHeadersPacket gh=(GetHeadersPacket) c.createPacket(PacketType.GETHEADERS);
			gh.startHashes().add(blockChain.topBlock().hash());
			c.sendPacket(gh);
		}
		
			
	}
	
	public void closeAll(){
		maintainThread.close();
		maintainThread.interrupt();
		for(Connection c : connections){
			c.close();
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

	public void setBlockChain(BlockChain blockChain) {
		this.blockChain = blockChain;
	}

	public BlockChain getBlockChain() {
		return blockChain;
	}
	
	private void removeConnectionsFromAddressList(List<Connection> connections, List<Address> addresses){
		for(Connection c : connections){
			addresses.remove(c.getAddress());
		}
	}
	
	private class MaintainConnectionsThread extends Thread {
	
		private boolean shouldClose=false;
		private ConnectionHandler ch;
		
		public MaintainConnectionsThread(ConnectionHandler ch){
			this.ch=ch;
		}
		
		public void run(){
			while(!shouldClose){
				ch.maintainConnections();
				try {
					sleep(500);
				} catch (InterruptedException e) {
					shouldClose=true;
				}
			}
		}
		
		public void close(){
			shouldClose=true;
		}
		
		
	
	}
	

}
