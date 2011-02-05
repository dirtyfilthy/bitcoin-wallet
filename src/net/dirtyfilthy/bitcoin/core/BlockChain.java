package net.dirtyfilthy.bitcoin.core;

import java.util.HashMap;
import java.util.Vector;
import java.math.BigInteger;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.MyHex;
public class BlockChain {
	private HashMap<BigInteger,BlockNode> hashMap=new HashMap<BigInteger,BlockNode>();
	private BigInteger highestTotalWork=BigInteger.ZERO;
	private BlockNode topBlockNode=null;
	private Vector<BlockNode> longestChain=null;
	
	
	public BlockChain(){
		Block genesis=ProtocolVersion.genesisBlock();
		BlockNode genesisNode=new BlockNode(genesis);
		hashMap.put(genesisNode.bigIntegerHash(), genesisNode);
		highestTotalWork=genesisNode.getTotalWork();
		topBlockNode=genesisNode;
		longestChain=genesisNode.chain();
	}
	
	public Block topBlock(){
		if(topBlockNode==null){
			return null;
		}
		return topBlockNode.block();
	}
	
	public boolean isKnown(Block block){
		BlockNode bn=new BlockNode(block);
		return hashMap.containsKey(bn.bigIntegerHash());
	}
	
	
	
	
	public synchronized BlockNode addBlock(Block block) throws InvalidBlockException{
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
		if(bn.block().getBits()!=prevBn.nextDifficulty()){
			throw new InvalidBlockException("Invalid difficulty");
		}
		System.out.println("current time   : "+bn.getTime());
		System.out.println("prev time      : "+prevBn.getMedianTimePast());
		if(bn.getTime()<=prevBn.getMedianTimePast()){
			throw new InvalidBlockException("Incorrect timestamp");
		}
		BigInteger totalWork=bn.getTotalWork();
		if(totalWork.compareTo(highestTotalWork)>0){
			highestTotalWork=totalWork;
			topBlockNode=bn;
			longestChain=bn.chain();
		}
		bn.setPrev(prevBn);
		return bn;
	}
	

	
}
