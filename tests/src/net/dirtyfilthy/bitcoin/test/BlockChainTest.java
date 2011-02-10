package net.dirtyfilthy.bitcoin.test;

import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.core.BlockChain;
import net.dirtyfilthy.bitcoin.core.InvalidBlockException;
import net.dirtyfilthy.bitcoin.core.OrphanBlockException;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import android.test.AndroidTestCase;

public class BlockChainTest extends AndroidTestCase {
	
	private BlockChain bc;
	
	public void setUp(){
		bc=new BlockChain();
		
	}
	
	public void testSimpleAddBlock() throws InvalidBlockException, OrphanBlockException{
		Block second=ProtocolVersion.secondBlock();
		
		
		//try {
			assertEquals("Genesis block doesn't have height 0",0,bc.topBlock().getHeight());
			Block b=bc.addBlock(second);
			assertEquals("Second block doesn't have height 1",1,b.getHeight());
		//} catch (InvalidBlockException e) {
			// TODO Auto-generated catch block
		//	fail("Invalid block exception generated");
		//}
	}

}
