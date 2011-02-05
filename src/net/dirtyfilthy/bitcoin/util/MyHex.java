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
	
	public static String encodePadded(byte[] bytes, int length){
		  		String s=encode(bytes);
		        StringBuffer sb = new StringBuffer(s);
		        int numZeros = (length*2) - s.length();
		        while(numZeros-- > 0) { 
		            sb.insert(0, "0");
		        }
		        return sb.toString();
		   

	}

}
