package net.dirtyfilthy.bitcoin.util;

import java.math.BigInteger;

public class BigIntegerTools {
	
	public static long compactBigInt(BigInteger i){
		byte[] b=i.toByteArray();
		int size=b.length;
		long compact = (size << 24) ;
		if(size>=1){
			compact |= ((long) (b[0] & 0xff) << 16);
		}
		if(size>=2){
			compact |= ((long) (b[1] & 0xff) << 8);
		}
		if(size>=3){
			compact |= ((long) (b[2] & 0xff) << 0);
		}
		return compact;
		
	}
	
	public static BigInteger uncompactBigInt(long compact){
		int leftShift=(int) (((compact >>> 24) & 0xff)-3)*8;
		BigInteger base=BigInteger.valueOf(compact & 0xffffffL);
		return base.shiftLeft(leftShift);
	}

}
