package net.dirtyfilthy.bitcoin.test;

import java.math.BigInteger;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.BigIntegerTools;
import net.dirtyfilthy.bitcoin.util.MyHex;
import android.test.AndroidTestCase;

public class BigIntegerToolsTest  extends AndroidTestCase {
	
	
	public void testSerializeUnserialize(){
		BigInteger pw=ProtocolVersion.proofOfWorkLimit();
		long compact=BigIntegerTools.compactBigInt(pw);
		BigInteger unserialized=BigIntegerTools.uncompactBigInt(compact);
		assertTrue("Unserialized BigInteger is not the same as the original",pw.compareTo(unserialized)==0);
	}

}
