package net.dirtyfilthy.bitcoin.test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import net.dirtyfilthy.bitcoin.protocol.GetHeadersPacket;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import android.test.AndroidTestCase;

public class GetHeadersTest extends AndroidTestCase{
	
	public void testSerializeUnserialize() throws IOException{
		GetHeadersPacket gh1=new GetHeadersPacket();
		gh1.startHashes().add(ProtocolVersion.genesisBlock().hash());
		byte[] b1=gh1.create();
		DataInputStream in=new DataInputStream(new ByteArrayInputStream(b1));
		GetHeadersPacket gh2=new GetHeadersPacket();
		gh2.readExternal(in);
		byte[] b2=gh2.create();
		assertTrue("Getheaders doesn't serialize/unserialize same way",Arrays.equals(b1, b2));
		
	}

}
