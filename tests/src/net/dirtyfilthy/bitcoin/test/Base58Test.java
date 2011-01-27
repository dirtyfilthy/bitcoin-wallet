package net.dirtyfilthy.bitcoin.test;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import net.dirtyfilthy.bitcoin.util.Base58;
import android.test.AndroidTestCase;

public class Base58Test extends AndroidTestCase {
	
	public void testEncode() throws UnsupportedEncodingException{
		String test="Mary had a little lamb";
		String result="N17bd3Q3H5kKmHYdGgZZ8mV5FmkDx5";
		byte[] rawIn=test.getBytes("ASCII");
		String encoded=Base58.encode(rawIn);
		assertEquals(result,encoded);
	}

	public void testEncodeDecode() throws ParseException, UnsupportedEncodingException{
		String test="Mary had a little lamb";
		byte[] rawIn=test.getBytes("ASCII");
		String encoded=Base58.encode(rawIn);
		System.out.println("Encoded: "+encoded);
		byte[] rawOut=Base58.decode(encoded);
		assertTrue("Doesn't encode/decode to same value", java.util.Arrays.equals(rawIn, rawOut));
		
	}
	
	public void testEncodeDecodeCheck() throws ParseException, UnsupportedEncodingException{
		String test="Mary had a little lamb";
		byte[] rawIn=test.getBytes("ASCII");
		String encoded=Base58.encodeCheck(rawIn);
		System.out.println("Encoded: "+encoded);
		byte[] rawOut=Base58.decodeCheck(encoded);
		assertTrue("Doesn't encode/decode to same value", java.util.Arrays.equals(rawIn, rawOut));
		
	}
	
	public void testDecodeBootstrapAddress() throws ParseException, UnsupportedEncodingException{
		byte raw[]=Base58.decodeCheck("5EhG6WPQEnLPQ6");
		assertEquals(6, raw.length); // four bytes ip, two port
	}
	
	
	
}
