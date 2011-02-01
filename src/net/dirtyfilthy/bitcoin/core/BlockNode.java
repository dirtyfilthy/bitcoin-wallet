package net.dirtyfilthy.bitcoin.core;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;

public class BlockNode {
	
	private BigInteger bigIntegerHash;
	private BigInteger previousBigIntegerHash;
	private Block block;
	private BlockNode next;
	private BlockNode prev;
	private int height=-1;
	private BigInteger totalWork;
	
	public BlockNode(Block block){
		this.block=block;
		bigIntegerHash=new BigInteger(block.hash());
		previousBigIntegerHash=new BigInteger(block.getPreviousHash());
	}
	
	public Block block(){
		return this.block;
	}

	public void setNext(BlockNode next) {
		this.next = next;
	}

	public BlockNode next() {
		return next;
	}

	public void setPrev(BlockNode prev) {
		this.prev = prev;
	}

	public BlockNode prev() {
		return prev;
	}
	
	public BigInteger nextDifficulty(){
		 long targetTimespan = ProtocolVersion.targetTimespan(); // two weeks
		 long targetSpacing = ProtocolVersion.targetInterval();
		 long interval = targetTimespan / targetSpacing;
		 if(this.prev()==null){
			 return ProtocolVersion.proofOfWorkLimit();
		 }
		 if((this.height()+1) % interval != 0){
			 return this.target();
		 }
		 BlockNode first=this;
		 for (int i = 0; i<interval-1; i++){
			 first=first.prev();
		 }
		 long actualTimespan=(this.getTime()-first.getTime())/1000;
		 if (actualTimespan < targetTimespan/4){
			 actualTimespan = targetTimespan/4;
		 }
		 if (actualTimespan > targetTimespan*4){
			 actualTimespan = targetTimespan*4;
		 }
		 BigInteger nextDifficulty=this.target().multiply(BigInteger.valueOf(targetTimespan));
		 nextDifficulty=nextDifficulty.divide(BigInteger.valueOf(actualTimespan));
		 if(nextDifficulty.compareTo(ProtocolVersion.proofOfWorkLimit())>0){
			 nextDifficulty=ProtocolVersion.proofOfWorkLimit();
		 }
		 return nextDifficulty;
	}
	
	public long getTime(){
		return block.getTimestamp().getTime();
	}
	
	public int height(){
		if(height==-1){
			if(this.prev()==null){
				height=0;
			}
			else{
				height=this.prev().height()+1;
			}
		}
		return height;
	}
	
	public long getMedianTimePast(){
		int timespan=ProtocolVersion.medianTimeSpan();
		Vector<Long> values=new Vector<Long>();
		BlockNode current=this;
		for(int i=0;i<timespan;i++){
			current=current.prev();
			if(current==null){
				break;
			}
			values.add(new Long(current.getTime()));
		}
		Collections.sort(values);
		return values.get(values.size()/2).longValue();
	}
	
	public BigInteger target(){
		return block.targetHash();
	}
	
	public Vector<BlockNode> chain(){
		Vector<BlockNode> vbn=new Vector<BlockNode>();
		vbn.add(this);
		if(this.prev()!=null){
			vbn.addAll(this.prev().chain());
		}
		
		return vbn;
	}
	
	public BigInteger getTotalWork(){
		if(totalWork!=null){
			return totalWork;
		}
		if(this.prev()==null){
			totalWork=block.getWork();
		}
		else{
			totalWork=block.getWork().add(this.prev().getTotalWork());
		}
		return totalWork;
	}
	
	
	public BigInteger bigIntegerHash(){
		return this.bigIntegerHash;
	}
	
	public BigInteger previousBigIntegerHash(){
		return this.previousBigIntegerHash;
	}
	
}
