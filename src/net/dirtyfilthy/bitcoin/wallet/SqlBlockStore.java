package net.dirtyfilthy.bitcoin.wallet;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.core.BlockChain;
import net.dirtyfilthy.bitcoin.core.BlockStore;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bouncycastle.util.Arrays;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;

public class SqlBlockStore extends BlockStore {
	private SQLiteDatabase db;
	private Block lastStored;
	private StringBuilder sql=new StringBuilder(200); 
	private LinkedList<Block> getByHashCache=new LinkedList<Block>();
	public SqlBlockStore(SQLiteDatabase db){
		super(false);
		this.db=db;
		Block genesis=ProtocolVersion.genesisBlock();
		db.beginTransaction();
		try {
			if(!has(genesis)){
				System.out.println("Storing genesis...");
				put(genesis);
				db.setTransactionSuccessful();
			}

		} finally {
			db.endTransaction();
		}

	}
	
	private Block createBlockFromCursor(Cursor cursor){
		Block b=new Block();
		b.setPreviousHash(cursor.getBlob(2));
		b.setMerkleRoot(cursor.getBlob(3));
		b.setHeight(cursor.getInt(4));
		b.setTotalWork(new BigInteger(cursor.getBlob(5)));
		b.setTimestamp(new java.util.Date(cursor.getLong(6)));
		b.setNonce(cursor.getLong(7));
		b.setBits(cursor.getLong(8));
		return b;
	}
	
	public synchronized boolean has(Block b){
		return getByHash(b.hash())!=null;
	}
	
	public synchronized Block getByHash(byte[] hash){
		
		// leetle bit of caching
		Block b;
		if(lastStored!=null &&  Arrays.areEqual(hash,lastStored.hash())){
			return lastStored;
		}
		b=searchGetByHashCache(hash);
		if(b!=null){
			return b;
		}
		sql.setLength(0);
		sql.append("select * from blocks where hash=X'");
		MyHex.encodeAppendStringBuilder(sql,hash);
		sql.append("'");
		Cursor cursor = db.rawQuery(sql.toString(),null); 
		if(cursor.getCount() == 0){
			cursor.close();
			return null;
		}
		cursor.moveToFirst();
		b=createBlockFromCursor(cursor);
		cursor.close();
		return b;
	}
	
	public synchronized Block getByPreviousHash(byte[] hash){
		sql.setLength(0);
		sql.append("select * from blocks where previousHash=X'");
		sql.append(MyHex.encode(hash));
		sql.append("'");
		Cursor cursor = db.rawQuery(sql.toString(),null); ; 
		if(cursor.getCount() == 0){
			return null;
		}
		cursor.moveToFirst();
		Block b=createBlockFromCursor(cursor);
		cursor.close();
		return b;
	}
	

	public synchronized Block getPrevious(Block b){
		return getByHash(b.getPreviousHash());
	}
	
	
	
	public synchronized Block put(Block b){
		Block prev=getPrevious(b);
		if(topBlock==null || getTotalWork(b).compareTo(topBlock.getTotalWork())>0){
			topBlock=b;
		}
		if(prev!=null){
			b.setHeight(getPrevious(b).getHeight()+1);
		}
		else{
			b.setHeight(0);
		}
		storeBlock(b);
		return b;
	}
	
	private Block searchGetByHashCache(byte[] hash){
		Block b;
		Iterator<Block> i=getByHashCache.iterator();
		while(i.hasNext()){
			b=i.next();
			if(Arrays.areEqual(hash, b.hash())){
				return b;
			}
		}
		return null;
		
	}

	private void storeBlock(Block b) {
		ContentValues blockValues = new ContentValues();
		blockValues.put("hash",b.hash());
		blockValues.put("previous_hash", b.getPreviousHash());
		blockValues.put("merkle_root", b.getMerkleRoot());
		blockValues.put("height",b.getHeight());
		blockValues.put("total_work",getTotalWork(b).toByteArray());
		blockValues.put("timestamp",b.getTime());
		blockValues.put("nonce", b.getNonce());
		blockValues.put("bits", b.getBits());
		db.insert("blocks", null, blockValues);
		lastStored=b;
		getByHashCache.offer(b);
		if(getByHashCache.size()>13){
			getByHashCache.poll();
		}
		
	}

}
