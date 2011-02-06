package net.dirtyfilthy.bitcoin.core;

import java.util.HashMap;
import java.util.Vector;
import java.math.BigInteger;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.MyHex;
public class BlockChain {
	private HashMap<BigInteger,BlockNode> hashMap=new HashMap<BigInteger,BlockNode>();
	private HashMap<BigInteger,Vector<Block>> orphanBlocks=new HashMap<BigInteger,Vector<Block>>();
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
	
	public BlockNode topBlockNode(){
		return topBlockNode;
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
	
	
	
	
	public synchronized BlockNode addBlock(Block block) throws InvalidBlockException, OrphanBlockException{
		if(!block.validProofOfWork()){
			System.out.println("FAILED PROOF OF WORK");
			System.out.println("bits   :"+Integer.toHexString((int) block.getBits()));
			System.out.println("bits %d:"+block.getBits());
			System.out.println("hash   :"+MyHex.encodePadded(block.hash(), 32));
			System.out.println("target :"+MyHex.encodePadded(block.targetHash().toByteArray(), 32));
			System.out.println("height :"+topBlockNode.height());
			throw new InvalidBlockException("Invalid proof of work");
		}
		BlockNode bn=new BlockNode(block);
		if(hashMap.containsKey(bn.bigIntegerHash())){
			throw new InvalidBlockException("Already added");
		}
		BigInteger prevHash=bn.previousBigIntegerHash();
		BlockNode prevBn=hashMap.get(prevHash);
		if(prevBn==null){
			if(orphanBlocks.containsKey(prevHash)){
				orphanBlocks.get(prevHash).add(block);
			}
			else{
				Vector<Block >vb=new Vector<Block>();
				orphanBlocks.put(prevHash,vb);
				vb.add(block);
			}
			throw new OrphanBlockException("Can't find previous block hash");
		}
		if(bn.block().getBits()!=prevBn.nextDifficulty()){
			throw new InvalidBlockException("Invalid difficulty");
		}
		if(bn.getTime()<=prevBn.getMedianTimePast()){
			throw new InvalidBlockException("Incorrect timestamp");
		}
		
	
	
		bn.setPrev(prevBn);
		hashMap.put(bn.bigIntegerHash(), bn);
		BigInteger totalWork=bn.getTotalWork();
		if(totalWork.compareTo(highestTotalWork)>0){
			highestTotalWork=totalWork;
			if(prevBn!=topBlockNode){
				// reorganize
			}
			topBlockNode=bn;
			longestChain=bn.chain();
		}
		if(orphanBlocks.containsKey(bn.bigIntegerHash())){
			Vector<Block> toAdd=orphanBlocks.get(bn.bigIntegerHash());
			orphanBlocks.remove(bn.bigIntegerHash());
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
		
		return bn;
	}
	

	
}
