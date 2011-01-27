package net.dirtyfilthy.bitcoin.core;

import java.util.HashMap;
import java.util.Vector;
import java.math.BigInteger;
public class BlockChain {
	private HashMap<BigInteger,BlockNode> hashMap=new HashMap<BigInteger,BlockNode>();
	private BigInteger highestTotalDifficulty=BigInteger.ZERO;
	private BlockNode topBlock=null;
	private Vector<BlockNode> longestChain=null;
	
	public BlockNode addBlock(Block block) throws InvalidBlockException{
		if(!block.validProofOfWork()){
			throw new InvalidBlockException("Invalid proof of work");
		}
		BlockNode bn=new BlockNode(block);
		if(hashMap.containsKey(bn.bigIntegerHash())){
			throw new InvalidBlockException("Already added");
		}
		
		BlockNode prevBn=hashMap.get(bn.previousBigIntegerHash());
		if(prevBn==null){
			throw new InvalidBlockException("Can't find previous block hash");
		}
		if(bn.difficulty().compareTo(prevBn.nextDifficulty())!=0){
			throw new InvalidBlockException("Invalid difficulty");
		}
		if(bn.getTime()>=prevBn.getMedianTimePast()){
			throw new InvalidBlockException("Incorrect timestamp");
		}
		BigInteger totalDifficulty=bn.getTotalDifficulty();
		if(totalDifficulty.compareTo(highestTotalDifficulty)>0){
			highestTotalDifficulty=totalDifficulty;
			topBlock=bn;
			longestChain=bn.chain();
		}
		bn.setPrev(prevBn);
		return bn;
	}
	

	
}
