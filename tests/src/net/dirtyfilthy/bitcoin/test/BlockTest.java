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
		System.out.println("genesis hash "+MyHex.encode(genesis.hash()));
		System.out.println("genesis timestamp "+genesis.getTimestamp());
		assertTrue("Genesis block has incorrect hash", Arrays.areEqual(genesis.hash(), hash));
	}
	
	public void testSerializeUnserialize() throws IOException{
		Block genesis=ProtocolVersion.genesisBlock();
		byte[] serialized=genesis.toByteArray(false);
		System.out.println("serialized1 size: "+serialized.length);
		System.out.println("serialized1: "+MyHex.encode(serialized));
		
		Block genesis2=new Block(new DataInputStream(new ByteArrayInputStream(serialized.clone())),false);
		byte[] serialized2=genesis2.toByteArray(false);
		System.out.println("serialized2: "+MyHex.encode(serialized2));
		assertEquals("version different", genesis.getBlockVersion(), genesis2.getBlockVersion());
		assertTrue("Prev hash different", Arrays.areEqual(genesis.getPreviousHash(), genesis2.getPreviousHash()));
		System.out.println("merkle1: "+MyHex.encode(genesis.getMerkleRoot()));
		System.out.println("merkle2: "+MyHex.encode(genesis2.getMerkleRoot()));
		System.out.println("merkle1 size: "+genesis.getMerkleRoot().length);
		System.out.println("merkle2 size: "+genesis2.getMerkleRoot().length);
		assertTrue("Merkle root different", Arrays.areEqual(genesis.getMerkleRoot(), genesis2.getMerkleRoot()));
		
		assertEquals("Timestamp different", genesis.getTimestamp().getTime(), genesis2.getTimestamp().getTime());
		assertEquals("Difficulty different", genesis.getBits(), genesis2.getBits());
		assertEquals("Nonce different", genesis.getNonce(), genesis2.getNonce());
		
		assertTrue("Block does not reserialize same way", Arrays.areEqual(serialized, serialized2));
	}
	
}
