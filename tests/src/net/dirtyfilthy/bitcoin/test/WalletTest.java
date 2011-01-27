package net.dirtyfilthy.bitcoin.test;

import java.io.FileNotFoundException;
import java.security.KeyStoreException;

import android.content.Context;
import net.dirtyfilthy.bitcoin.wallet.InvalidPasswordException;
import net.dirtyfilthy.bitcoin.wallet.Wallet;
import junit.framework.TestCase;

public class WalletTest extends android.test.AndroidTestCase {
	
	private Context context;
	private String db_file;
	
	public void setUp(){
		context=getContext();
		db_file="test.db";
	}
	
	public void testCreateWallet(){
		
		try {
			Wallet w=new Wallet(context,"test.db","password");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPasswordException e) {
			fail("Invalid password???");
		}
	}
	
	public void tearDown(){
		context.deleteFile(db_file);
	}
	
}
