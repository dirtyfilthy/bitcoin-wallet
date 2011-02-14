package net.dirtyfilthy.bitcoin.core;

import java.math.BigInteger;
import java.util.HashMap;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;

// in memory block store, this will quickly exhaust  the 16mb heap under android

public class BlockStore {
	
	private HashMap<BigInteger,Block> byHash=new HashMap<BigInteger,Block>();
	private HashMap<BigInteger,Block> byPrev=new HashMap<BigInteger,Block>();
	protected Block topBlock;
	protected BigInteger highestWork; 
	
	public BlockStore(){
		this(true);
	}
	
	public BlockStore(boolean addGenesis){
		if(addGenesis){
			try {
				put(ProtocolVersion.genesisBlock());
			} catch (BlockExistsException e) {
				// do nothing
			}
		}
	}
	
	public Block top(){
		return topBlock;
	}
	
	
	public synchronized Block put(Block b) throws BlockExistsException {
		byHash.put(new BigInteger(b.hash()),b);
		byPrev.put(new BigInteger(b.getPreviousHash()),b);
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
		return b;
	}
	
	public boolean has(Block b){
		return getByHash(b.hash())!=null;
	}
	
	public Block getByHash(byte[] hash){
		return byHash.get(new BigInteger(hash));
	}
	
	public Block getByPreviousHash(byte[] prev){
		return byPrev.get(new BigInteger(prev));
	}
	
	public Block getPrevious(Block b){
		return getByHash(b.getPreviousHash());
	}
	

	public BigInteger getTotalWork(Block b){
		BigInteger totalWork;
		if(b.getTotalWork()!=null){
			return b.getTotalWork();
		}
		if(getPrevious(b)==null){
			totalWork=b.getWork();
		}
		else{
			totalWork=b.getWork().add(getTotalWork(getPrevious(b)));
		}
		b.setTotalWork(totalWork);
		return totalWork;
	}
	
	
}
