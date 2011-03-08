package net.dirtyfilthy.bitcoin.util;

import java.math.BigInteger;
import java.text.ParseException;


public class Base58 {
	public final static String BASE58CHARS="123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
	public final static BigInteger FIFTY_EIGHT=new BigInteger("58");
	
	/**
	 * Takes a series of bytes and return a bitcoin base58 encoded string
	 * @param bytes
	 * Array of bytes to include
	 * @return
	 * Base58 encoded string
	 */
	
	public static String encode(byte[] bytes){
		byte[] extra_zero=new byte[bytes.length+1];
		String result="";
		extra_zero[0]=0;
		
		System.arraycopy(bytes,0,extra_zero,1,bytes.length);
		BigInteger bn=new BigInteger(extra_zero);
		while(bn.compareTo(BigInteger.ZERO)==1){
			BigInteger div[]=bn.divideAndRemainder(FIFTY_EIGHT);
			bn=div[0];
			char c=BASE58CHARS.charAt(div[1].intValue());
			result=result+c;
		}
		
		// pad leading zeros
		result=new String(new StringBuffer(result).reverse());
		for(int i=0;bytes[i]==0;i++){
			result=BASE58CHARS.charAt(0)+result;
		}
		return result;
	}
	
	/**
	 * Decode a bitcoin encoded base58 string
	 * @param encoded
	 * Base58 encoded string
	 * @return
	 * The decoded byte array
	 * @throws ParseException
	 */
	
	public static byte[] decode(String encoded) throws ParseException{
		BigInteger bn=BigInteger.ZERO;
		BigInteger mult;
		String reversed=new String(new StringBuffer(encoded).reverse());
		byte[] raw;
		for(int pos=0;pos<reversed.length();pos++){
			int val=BASE58CHARS.indexOf(reversed.charAt(pos));
			
			if(val==-1){
				throw new ParseException(reversed, pos);
			}
			mult=FIFTY_EIGHT.pow(pos);
			bn=bn.add(mult.multiply(BigInteger.valueOf(val)));
		}
		
		raw=bn.toByteArray();
		if(raw[0]==0){
			byte[] raw2=new byte[raw.length-1];
			System.arraycopy(raw, 1, raw2, 0, raw.length-1);
			raw=raw2;
		}
		int leadingZeroes=0;
		for(int pos=0;((encoded.charAt(pos)=='1') && pos<encoded.length());pos++){
			leadingZeroes++;
		}
		byte[] fin=new byte[raw.length+leadingZeroes];
		System.arraycopy(raw, 0, fin, leadingZeroes, raw.length);
		return fin;
	}
	
	/**
	 * Decodes a base58 encoded string with a checksum
	 * @param encoded
	 * The base58 encoded string with checksum
	 * @return
	 * The decoded byte array
	 * @throws ParseException
	 */
	
	public static byte[] decodeCheck(String encoded) throws ParseException{
		byte[] raw=decode(encoded);
		if(raw.length<4){
			throw new ParseException("Decoded bytes too small for checksum",0);
		}
		byte[] decoded=new byte[raw.length-4];
		System.arraycopy(raw,0,decoded,0,raw.length-4);
		byte[] hash=HashTools.doubleSha256(decoded);
		for(int i=0;i<4;i++){
			if(hash[i]!=raw[decoded.length+i]){
				throw new ParseException("Incorrect checksum",decoded.length);
			}
		}
		return decoded;
	}
	
	/**
	 * Encodes a byte array into a base58 string with a checksum
	 * @param bytes
	 * The byte array to encode
	 * @return
	 * The base58 encoded string with a checksum
	 */
	
	public static String encodeCheck(byte[] bytes){
		byte[] hash=HashTools.doubleSha256(bytes);
		byte[] toHash=new byte[bytes.length+4];
		System.arraycopy(bytes, 0, toHash, 0, bytes.length);
		System.arraycopy(hash, 0, toHash, bytes.length, 4);
		return encode(toHash);
	}

}
