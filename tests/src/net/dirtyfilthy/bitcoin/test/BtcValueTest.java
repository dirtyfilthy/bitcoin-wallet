package net.dirtyfilthy.bitcoin.test;

import net.dirtyfilthy.bitcoin.core.BtcValue;
import net.dirtyfilthy.bouncycastle.util.Arrays;


import android.test.AndroidTestCase;

public class BtcValueTest extends AndroidTestCase {
	
	public void testFromDouble(){
		byte[] expected={(byte) 0x80, (byte) 0xFA, (byte) 0xE9, (byte) 0xC7, 00, 00, 00, 00};     
		BtcValue v=new BtcValue(33.54);
		assertTrue("BtcValue does not return expected byte array",Arrays.areEqual(expected, v.toByteArray()));
	}

}
