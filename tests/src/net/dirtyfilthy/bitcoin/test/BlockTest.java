package net.dirtyfilthy.bitcoin.test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.dirtyfilthy.bitcoin.core.Block;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bitcoin.util.QuickHash;
import net.dirtyfilthy.bouncycastle.util.Arrays;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;
import android.test.AndroidTestCase;

public class BlockTest  extends AndroidTestCase {

	public void testGenesisBlock(){
		Block genesis=ProtocolVersion.genesisBlock();
		byte hash[]=QuickHash.reverseByteArray(Hex.decode("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"));
		assertTrue("Genesis block has incorrect hash", Arrays.areEqual(genesis.hash(), hash));
	}
	
	public void testSecondBlock(){
		Block second=ProtocolVersion.secondBlock();
		byte hash[]=QuickHash.reverseByteArray(Hex.decode("00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048"));
		assertTrue("Second block has incorrect hash", Arrays.areEqual(second.hash(), hash));
	}
	
	
	
	public void testSerializeUnserialize() throws IOException{
		Block genesis=ProtocolVersion.genesisBlock();
		byte[] serialized=genesis.toByteArray(false);
		
		Block genesis2=new Block(new DataInputStream(new ByteArrayInputStream(serialized.clone())),false);
		byte[] serialized2=genesis2.toByteArray(false);
		assertEquals("version different", genesis.getBlockVersion(), genesis2.getBlockVersion());
		assertTrue("Prev hash different", Arrays.areEqual(genesis.getPreviousHash(), genesis2.getPreviousHash()));
		assertTrue("Merkle root different", Arrays.areEqual(genesis.getMerkleRoot(), genesis2.getMerkleRoot()));
		
		assertEquals("Timestamp different", genesis.getTimestamp().getTime(), genesis2.getTimestamp().getTime());
		assertEquals("Difficulty different", genesis.getBits(), genesis2.getBits());
		assertEquals("Nonce different", genesis.getNonce(), genesis2.getNonce());
		
		assertTrue("Block does not reserialize same way", Arrays.areEqual(serialized, serialized2));
		Block second=ProtocolVersion.secondBlock();
		
		// test second block
		
		byte[] s_serialized=second.toByteArray(false);
		
		Block second2=new Block(new DataInputStream(new ByteArrayInputStream(s_serialized.clone())),false);
		byte[] s_serialized2=second2.toByteArray(false);
		assertEquals("second block version different", second.getBlockVersion(), second2.getBlockVersion());
		
		assertTrue("second block Prev hash different", Arrays.areEqual(second.getPreviousHash(), second2.getPreviousHash()));
		assertTrue("second block Merkle root different", Arrays.areEqual(second.getMerkleRoot(), second2.getMerkleRoot()));
		
		assertEquals("second block Timestamp different", second.getTimestamp().getTime(), second2.getTimestamp().getTime());
		assertEquals("second block Difficulty different", second.getBits(), second2.getBits());
		assertEquals("second block Nonce different", second.getNonce(), second2.getNonce());
		
		assertTrue("second Block does not reserialize same way", Arrays.areEqual(s_serialized, s_serialized2));
		
	}
	
}
