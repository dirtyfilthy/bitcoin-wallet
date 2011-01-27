package net.dirtyfilthy.bitcoin.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import android.util.Log;

import net.dirtyfilthy.bitcoin.core.Address;
import net.dirtyfilthy.bitcoin.util.Base58;

public class IrcBootStrap {
	private Socket socket;
	private PrintStream out;
	private DataInputStream in;
	private String host,channel;
	private int port;
	private String TAG="IrcBootStrap";
	
	public IrcBootStrap(String host,int port,String channel) throws UnknownHostException, IOException, InterruptedException{
		this.host=host;
		this.port=port;
		this.channel=channel;
		
	}	
		
		
		
	public Vector<Address> getAddresses() throws InterruptedException, IOException{
		socket=new Socket(host,port);
		out=new PrintStream( socket.getOutputStream());
		in=new DataInputStream(socket.getInputStream());
		Vector<Address> addresses=new Vector<Address>();
		String nickname="x"+new Random().nextInt();
		Log.d(TAG,"Registering");
		out.println( "USER " + nickname + " 8 * : "+ nickname );
	    out.println( "NICK" + " " + nickname);
	    Log.d(TAG,"Starting IRC thread");
	    IrcThread t=new IrcThread(in,out,channel);
	    t.start();
	    int timeout=20000, wait=500;
	    for(int current=0;current<timeout;current=current+wait){
	    	Log.d(TAG,"Looping until finished, current "+current);
	    	if(!t.isAlive() || t.finished()){
	    		break;
	    	}
	    	Thread.sleep(wait);
	    }
	    t.finish();
	    out.println( "QUIT" );
	    socket.close();
	    Log.d(TAG,"Decoding");
	    Vector<String> encoded=t.getEncodedAddresses();
	    for(String addr : encoded){
	    
	    	if(addr.charAt(0)!='u'){
	    		continue;
	    	}
	    	addr=addr.substring(1);
	    	try {
				addresses.add(decodeAddress(addr));
			} catch (ParseException e) {
				continue;
			}
	    	
	    }
		return addresses;
	}
	
	public Address decodeAddress(String encoded) throws UnknownHostException, ParseException{
		byte raw[]=Base58.decodeCheck(encoded);
		if(raw.length!=6){
			throw new ParseException("Decoded bytes are the wrong length.",raw.length);
		}
		byte[] addrIp=new byte[4];
		System.arraycopy(raw, 0, addrIp, 0, 4);
		int addrPort=(256*((int) raw[4] & 0xff))+((int) raw[5] & 0xff);
		Address a=new Address(InetAddress.getByAddress(addrIp),addrPort);
		return a;
	}
	
	
	
	private class IrcThread extends Thread {
		private DataInputStream in;
		private PrintStream out;
		private String channel;
		private Vector<String> encodedAddresses=new Vector<String>();
		private boolean finished=false;
		private boolean shouldFinish=false;
		IrcThread(DataInputStream in, PrintStream out, String channel){
			this.in=in;
			this.out=out;
			this.channel=channel;
			
		}
		
		public synchronized boolean finished(){
			return finished;
		}
		
		public synchronized boolean finish(){
			return shouldFinish=true;
		}
		
		
		public void run(){
			String line;
			while(!shouldFinish){
				try {
					line=in.readLine();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					break;
				}
				String code=line.split("\\s")[1];
				if(code.equals("001")){
					out.println("JOIN "+channel);
				}
				if(code.equals("353")){
					String nicks=line.split(":")[2];
					encodedAddresses.addAll(Arrays.asList(nicks.split("\\s")));
				}
				if(code.equals("366")){
					break;
				}
				
				
				
			}
			finished=true;
			return;
		}

		@SuppressWarnings("unchecked")
		public synchronized Vector<String> getEncodedAddresses() {
			return (Vector<String>) encodedAddresses.clone();
		}
	}
		

}
