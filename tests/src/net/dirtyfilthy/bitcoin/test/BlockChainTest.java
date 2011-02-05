package net.dirtyfilthy.bitcoin.test;

import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.core.BlockChain;
import net.dirtyfilthy.bitcoin.core.InvalidBlockException;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import android.test.AndroidTestCase;

public class BlockChainTest extends AndroidTestCase {
	
	private BlockChain bc;
	
	public void setUp(){
		bc=new BlockChain();
		
	}
	
	public void testSimpleAddBlock(){
		Block second=ProtocolVersion.secondBlock();
		
		try {
			bc.addBlock(second);
		} catch (InvalidBlockException e) {
			// TODO Auto-generated catch block
			fail("Invalid block exception generated");
		}
	}

}
