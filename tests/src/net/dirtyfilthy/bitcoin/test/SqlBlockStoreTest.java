package net.dirtyfilthy.bitcoin.test;

import java.io.FileNotFoundException;
import java.security.KeyStoreException;
import java.util.Arrays;

import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.core.BlockExistsException;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.wallet.InvalidPasswordException;
import net.dirtyfilthy.bitcoin.wallet.SqlBlockStore;
import net.dirtyfilthy.bitcoin.wallet.Wallet;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class SqlBlockStoreTest extends AndroidTestCase {
	private Context context;
	private String db_file;
	private SQLiteDatabase db;
	private SqlBlockStore bs;
	public void setUp(){
		
		context=getContext();
		db_file="test.db";
		context.deleteDatabase(db_file);
		
		try {
			Wallet w=new Wallet(context,db_file,"password");
			db=w.getDb();
			bs=w.getBlockStore();
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
		db.close();
		context.deleteDatabase(db_file);
	}
	
	
	public void testAddSecondRetrieveGenesis() throws BlockExistsException{
		Block genesis=ProtocolVersion.genesisBlock();
		Block second=ProtocolVersion.secondBlock();
		bs.put(second);
		Block genesisFromDb=bs.getByHash(genesis.hash());
		assertTrue("Genesis hash doesn't match on retrieval",Arrays.equals(genesisFromDb.hash(), genesis.hash()));
		Block secondFromDb=bs.getByHash(second.hash());
		assertTrue("Second hash doesn't match on retrieval",Arrays.equals(secondFromDb.hash(), second.hash()));
		
	}
	

}
