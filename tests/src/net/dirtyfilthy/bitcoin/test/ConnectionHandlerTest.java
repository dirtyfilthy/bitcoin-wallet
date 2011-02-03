package net.dirtyfilthy.bitcoin.test;

import java.net.UnknownHostException;

import net.dirtyfilthy.bitcoin.protocol.ConnectionHandler;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import android.test.AndroidTestCase;

public class ConnectionHandlerTest extends AndroidTestCase {
	
	public ConnectionHandler ch;
	
	public void setUp(){
		ProtocolVersion.useTestNet(false);
		try {
			ch=new ConnectionHandler();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void tearDown(){
		ch.closeAll();
	}
	
	public void testMaintainConnections() throws InterruptedException{
		ch.run();
		Thread.sleep(30 * 1000);
		assertEquals(5,ch.getConnectionsNumber());
	}
	
	public void testDownloadHeaders() throws InterruptedException{
		ch.run();
		System.out.println("after run");
		Thread.sleep(120 * 1000);
		System.out.println();
		System.out.println();
		System.out.println("connections: "+ch.getConnectionsNumber());
		System.out.println();
		System.out.println();
		ch.getInitialHeaders();
		Thread.sleep(120 * 1000);
	}
	
	

}
