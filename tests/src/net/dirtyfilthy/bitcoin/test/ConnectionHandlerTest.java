package net.dirtyfilthy.bitcoin.test;

import java.net.UnknownHostException;

import net.dirtyfilthy.bitcoin.protocol.ConnectionHandler;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import android.test.AndroidTestCase;

public class ConnectionHandlerTest extends AndroidTestCase {
	
	public ConnectionHandler ch;
	
	public void setUp(){
		ProtocolVersion.useTestNet(true);
		try {
			ch=new ConnectionHandler();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testMaintainConnections() throws InterruptedException{
		ch.maintainConnections();
		Thread.sleep(30 * 1000);
		assertEquals(5,ch.getConnectionsNumber());
	}

}
