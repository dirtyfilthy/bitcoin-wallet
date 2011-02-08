package net.dirtyfilthy.bitcoin.core;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.BigIntegerTools;

public class BlockNode {
	

	private Block block;
	private BlockNode next;
	private BlockNode prev;
	private int height=-1;
	private BigInteger totalWork;
	
	public BlockNode(Block block){
		this.block=block;
	
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
	
	
	

	
	public long nextDifficulty(){
		 long targetTimespan = ProtocolVersion.targetTimespan(); // two weeks
		 long targetSpacing = ProtocolVersion.targetInterval();
		 long interval = targetTimespan / targetSpacing;
		 if(this.prev()==null){
			 return BigIntegerTools.compactBigInt(ProtocolVersion.proofOfWorkLimit());
		 }
		 if((this.height()+1) % interval != 0){
			 return this.block.getBits();
		 }
		
		 BlockNode first=this;
		 for (int i = 0; i<interval-1; i++){
			 if(first.prev()==null){
				 break;
			 }
			 first=first.prev();
		 }
		 long actualTimespan=(this.getTime()-first.getTime())/1000;
		 if (actualTimespan < targetTimespan/4){
			 actualTimespan = targetTimespan/4;
		 }
		 if (actualTimespan > targetTimespan*4){
			 actualTimespan = targetTimespan*4;
		 }
		 BigInteger nextDifficulty=this.target().multiply(BigInteger.valueOf(actualTimespan));
		 nextDifficulty=nextDifficulty.divide(BigInteger.valueOf(targetTimespan));
		 if(nextDifficulty.compareTo(ProtocolVersion.proofOfWorkLimit())>0){
			 nextDifficulty=ProtocolVersion.proofOfWorkLimit();
		 }
		 return BigIntegerTools.compactBigInt(nextDifficulty);
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
		long time;
		long temp;
		long[] values=new long[timespan];
		BlockNode current=this;
		int j;
		// insertion sort this motherfucker
		
		for(int i=0;i<timespan;i++){
			time=current.getTime();
			current=current.prev();
			if(current==null){
				timespan=i+1;
				break;
			}
			values[i]=time;
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
	
	
}
