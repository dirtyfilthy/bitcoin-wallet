package net.dirtyfilthy.bitcoin.test;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.security.KeyStoreException;

import net.dirtyfilthy.bitcoin.core.Address;
import net.dirtyfilthy.bitcoin.protocol.ConnectionHandler;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.wallet.InvalidPasswordException;
import net.dirtyfilthy.bitcoin.wallet.Wallet;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Debug;
import android.test.AndroidTestCase;

public class ConnectionHandlerTest extends AndroidTestCase {
	
	private ConnectionHandler ch;
	private Context context;
	private String db_file;
	private Wallet wallet;
	
	public void setUp(){
		ProtocolVersion.useTestNet(false);
		
		
		context=getContext();
		db_file="test4.db";
		context.deleteDatabase(db_file);
		try {
			wallet=new Wallet(context,db_file,"password");
			ch=wallet.getConnectionHandler();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPasswordException e) {
			fail("Invalid password???");
		} catch (SQLiteConstraintException e) {
			throw e;
		}
		
		
	}
	
	public void tearDown(){
		System.out.println("tearing down..");
		ch.closeAll();
		wallet.close();
		//context.deleteDatabase(db_file);
		
	}
	
	public void testMaintainConnections() throws InterruptedException{
		ch.run();
		Thread.sleep(30 * 1000);
		assertEquals(5,ch.getConnectionsNumber());
	}
	
	public void testDownloadHeaders() throws InterruptedException, UnknownHostException{
		ch.setIrcBootStrap(false);
		ch.addAddress(new Address("192.168.1.9",8333));
		ch.run();
		System.out.println("after run");
		Thread.sleep(20 * 1000);
		System.out.println();
		System.out.println();
		System.out.println("connections: "+ch.getConnectionsNumber());
		System.out.println();
		System.out.println();
		ch.getInitialHeaders();
		
		Thread.sleep(1000 * 1000);
	}
	
	

}
