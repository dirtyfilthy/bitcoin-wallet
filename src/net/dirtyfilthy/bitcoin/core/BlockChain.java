package net.dirtyfilthy.bitcoin.core;

import java.util.HashMap;
import java.util.Vector;
import java.math.BigInteger;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.BigIntegerTools;
import net.dirtyfilthy.bitcoin.util.MyHex;
public class BlockChain {
	private HashMap<BigInteger,Vector<Block>> orphanBlocks=new HashMap<BigInteger,Vector<Block>>();
	private BlockStore blockStore;
	
	public BlockChain(){
		blockStore=new BlockStore();
	}
	
	
	public Block topBlock(){
		return blockStore.top();
	}
	
	public void setBlockStore(BlockStore blockStore) {
		this.blockStore = blockStore;
	}

	public BlockStore getBlockStore() {
		return blockStore;
	}

	public boolean isKnown(Block block){
		return blockStore.has(block);
	}
	
	
	
	
	public synchronized Block addBlock(Block block) throws InvalidBlockException, OrphanBlockException{
		if(!block.validProofOfWork()){
			System.out.println("FAILED PROOF OF WORK");
			System.out.println("bits   :"+Integer.toHexString((int) block.getBits()));
			System.out.println("bits %d:"+block.getBits());
			System.out.println("hash   :"+MyHex.encodePadded(block.hash(), 32));
			System.out.println("target :"+MyHex.encodePadded(block.targetHash().toByteArray(), 32));
			System.out.println("height :"+blockStore.top().getHeight());
			throw new InvalidBlockException("Invalid proof of work");
		}
		
		if(blockStore.has(block)){
			throw new InvalidBlockException("Already added");
		}
		BigInteger prevHash=block.previousBigIntegerHash();
		Block prev=blockStore.getPrevious(block);
		if(prev==null){
			if(orphanBlocks.containsKey(prevHash)){
				orphanBlocks.get(prevHash).add(block);
			}
			else{
				Vector<Block> vb=new Vector<Block>();
				orphanBlocks.put(prevHash,vb);
				vb.add(block);
			}
			throw new OrphanBlockException("Can't find previous block hash");
		}
		if(block.getBits()!=nextDifficulty(prev)){
			System.out.println("INVALID DIFFICULTY WORK");
			System.out.println("bits   :"+Integer.toHexString((int) block.getBits()));
			System.out.println("next   :"+Integer.toHexString((int) nextDifficulty(prev)));
			System.out.println("bits %d:"+block.getBits());
			System.out.println("hash   :"+MyHex.encodePadded(block.hash(), 32));
			System.out.println("target :"+MyHex.encodePadded(block.targetHash().toByteArray(), 32));
			System.out.println("height :"+blockStore.top().getHeight());
			throw new InvalidBlockException("Invalid difficulty");
		}
		if(block.getTime()<=getMedianTimePast(prev)){
			System.out.println("height :"+blockStore.top().getHeight());
			System.out.println("time :"+block.getTimestamp());
			throw new InvalidBlockException("Incorrect timestamp");
		}
		
	
	
		
		Block lastTop=blockStore.top();
		blockStore.put(block);
		Block top=blockStore.top();
		if(lastTop!=top && lastTop!=prev){
				// reorganize
		}
		
		if(orphanBlocks.containsKey(block.bigIntegerHash())){
			Vector<Block> toAdd=orphanBlocks.remove(block.bigIntegerHash());
			for(Block b : toAdd){
				
				// don't catch OrphanBlockExceptions here, they should never occur
				
				try{
					this.addBlock(b);
				}
				catch(InvalidBlockException e){
					// ignore
					continue;
				}
			}
		}
		if(block.getHeight() % 100 == 0){
			System.out.println("New height "+blockStore.top().getHeight());
		}
		return block;
	}
	
	
	public long nextDifficulty(Block b){
		 long targetTimespan = ProtocolVersion.targetTimespan(); // two weeks
		 long targetSpacing = ProtocolVersion.targetInterval();
		 long interval = targetTimespan / targetSpacing;
		 if(blockStore.getPrevious(b)==null){
			 return BigIntegerTools.compactBigInt(ProtocolVersion.proofOfWorkLimit());
		 }
		 if((b.getHeight()+1) % interval != 0){
			 return b.getBits();
		 }
		
		 Block first=b;
		 Block prev;
		 for (int i = 0; i<interval-1; i++){
			 prev=blockStore.getPrevious(first);
			 if(prev==null){
				 break;
			 }
			 first=prev;
		 }
		 long actualTimespan=(b.getTime()-first.getTime())/1000;
		 if (actualTimespan < targetTimespan/4){
			 actualTimespan = targetTimespan/4;
		 }
		 if (actualTimespan > targetTimespan*4){
			 actualTimespan = targetTimespan*4;
		 }
		 BigInteger nextDifficulty=b.targetHash().multiply(BigInteger.valueOf(actualTimespan));
		 nextDifficulty=nextDifficulty.divide(BigInteger.valueOf(targetTimespan));
		 if(nextDifficulty.compareTo(ProtocolVersion.proofOfWorkLimit())>0){
			 nextDifficulty=ProtocolVersion.proofOfWorkLimit();
		 }
		 return BigIntegerTools.compactBigInt(nextDifficulty);
	}
	
	public long getMedianTimePast(Block b){
		int timespan=ProtocolVersion.medianTimeSpan();
		long time;
		long temp;
		long[] values=new long[timespan];
		Block current=b;
		int j;
		// insertion sort this motherfucker
		
		for(int i=0;i<timespan;i++){
			time=current.getTime();
			
			values[i]=time;
			current=blockStore.getPrevious(b);
			if(current==null){
				timespan=i+1;
				break;
			}
			
			if(i==0){
				continue;
			}
			
			for (j = i; j > 0; j--) {
	            if (values[j-1] > values[j]) {
	               temp = values[j];
	               values[j] = values[j-1];
	               values[j-1] = temp;
	            }
	         }
		}
		return values[timespan/2];
	}


	
}
