package net.dirtyfilthy.bitcoin.util;

public class MyHex {
	public static String encode(byte[] bytes)
	    {
			String encoded="";
	        int cBytes = bytes.length;
	        int iByte = 0;

	        for (;;) {
	                String hex = Integer.toHexString(bytes[iByte++] & 0xff);
	                if (hex.length() == 1) {
	                    hex = "0" + hex;
	                }

	                encoded=encoded+hex;
	                if (iByte >= cBytes) {
	                    return encoded;
	                }
	        } 
	    }

}
