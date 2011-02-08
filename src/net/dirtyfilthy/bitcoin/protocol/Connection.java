package net.dirtyfilthy.bitcoin.protocol;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.dirtyfilthy.bitcoin.core.Address;
import net.dirtyfilthy.bitcoin.util.MyHex;

public class Connection implements Runnable {
	private Socket socket;
	private Address address;
	private DataInputStream in;
	private DataOutputStream out;
	private PacketFactory packetFactory;
	private ConnectionHandler connectionHandler;
	private long version=ProtocolVersion.version();
	private boolean hasReceivedVersion=false;
	private boolean hasReceivedVerack=false;
	private Thread mainLoop;
	private PacketQueue packetQueue; 
	private boolean close=false;
	
	
	Connection(ConnectionHandler ch, Address address) throws IOException{
		this.address=address;
		this.connectionHandler=ch;
		this.packetFactory=new PacketFactory();
		this.packetQueue=new PacketQueue(this,connectionHandler);
	}
	
	private void startMainLoop(){
		mainLoop=new Thread(this);
		mainLoop.start();
	}
	
	public void connect() {
		
		startMainLoop();
		
	}
	
	
	public Packet createPacket(PacketType type){
		return packetFactory.create(type);
	}
	
	public void sendPacket(Packet p) {
		packetQueue.offer(p);
	}

	public void run() {
		try {
			socket=new Socket(address.getIp(),address.getPort());
			in=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			out=new DataOutputStream(socket.getOutputStream());
			if(in==null || out==null){
				throw new IOException();
			}
			this.packetQueue.start();
		}
		catch (IOException e1) {
			connectionHandler.handleConnectionError(this, e1);
			return;
		}
		while(!close){
			try {
				
				if(in.available()==0){
					Thread.sleep(500);
					continue;
				}
				Packet p=packetFactory.readPacket(in);
				connectionHandler.handlePacket(this,p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				connectionHandler.handleConnectionError(this, e);
			} catch (InterruptedException e) {
				break;
			}
			
		}
		
	}
	
	public void close(){
		this.close=true;
		this.mainLoop.interrupt();
		this.packetQueue.interrupt();
		try {
			if(this.socket!=null)
				this.socket.close();
		} catch (IOException e) {
			// don't care do nothing
		}
	}

	public void setVersion(long negotiatedVersion) {
		this.version = negotiatedVersion;
		packetFactory.setVersion(negotiatedVersion);
	}

	public long getVersion() {
		return version;
	}

	public void hasReceivedVersion(boolean hasReceivedVersion) {
		this.hasReceivedVersion = hasReceivedVersion;
	}

	public boolean hasReceivedVersion() {
		return this.hasReceivedVersion;
	}
	
	public Address getAddress(){
		return this.address;
	}

	public void hasRecievedVerack(boolean hasReceivedVerack) {
		this.hasReceivedVerack = hasReceivedVerack;
	}

	public boolean hasRecievedVerack() {
		return hasReceivedVerack;
	}
	
	private class PacketQueue extends Thread {
		private DataOutputStream out;
		private ConcurrentLinkedQueue<Packet> queue;
		private Connection c;
		private ConnectionHandler ch;
		private boolean close=false;
		
		public PacketQueue(Connection c, ConnectionHandler ch){
			this.c=c;
			this.ch=ch;
			this.queue=new ConcurrentLinkedQueue<Packet>();
		}
		
		public boolean offer(Packet p){
			queue.offer(p);
			return true;
		}
		
		public void run(){
			this.out=this.c.out;
			while(!this.c.close){
				Packet toSend=queue.poll();
				if(toSend==null){
					try {
						Thread.sleep(200);
						continue;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						close=true;
						break;
					}
				}
				try {
					
					toSend.writeExternal(this.out);
					System.out.println("sending packet: "+toSend);
					System.out.println("connection: "+this.c);
					if(toSend.getCommand().equals("getheaders")){
						System.out.println("getheaders packet: "+MyHex.encode(toSend.toByteArray()));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					ch.handleConnectionError(c,e);
				}
		
					
			}
		
		}
		
	}
	
}
