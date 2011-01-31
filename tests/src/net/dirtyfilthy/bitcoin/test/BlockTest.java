package net.dirtyfilthy.bitcoin.test;

import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bouncycastle.util.Arrays;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;
import android.test.AndroidTestCase;

public class BlockTest  extends AndroidTestCase {

	public void testGenesisBlock(){
		Block genesis=ProtocolVersion.genesisBlock();
		byte hash[]=Hex.decode("0x000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f");
		System.out.println("genesis hash "+MyHex.encode(genesis.hash()));
		System.out.println("genesis timestamp "+genesis.getTimestamp());
		assertTrue("Genesis block has incorrect hash", Arrays.areEqual(genesis.hash(), hash));
	}
	
}
