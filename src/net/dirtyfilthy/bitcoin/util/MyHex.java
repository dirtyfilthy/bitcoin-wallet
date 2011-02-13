package net.dirtyfilthy.bitcoin.util;

public class MyHex {
	
	public static StringBuilder encoded=new StringBuilder(200);
	public static final String HEX_DIGITS="0123456789ABCDEF";
	
	
	public static synchronized String encode(byte[] bytes)
	    {
			
			encoded.setLength(0);
	        int cBytes = bytes.length;
	        int iByte = 0;
	        int b;

	        for (;;) {
	                b=bytes[iByte++] & 0xff;
	                encoded.append(HEX_DIGITS.charAt(b / 16));
	                encoded.append(HEX_DIGITS.charAt(b % 16));
	                if (iByte >= cBytes) {
	                    return encoded.toString();
	                }
	        } 
	    }
	
	public static synchronized void encodeAppendStringBuilder(StringBuilder builder, byte[] bytes){
        int cBytes = bytes.length;
        int iByte = 0;
        int b;

        for (;;) {
                b=bytes[iByte++] & 0xff;
                builder.append(HEX_DIGITS.charAt(b / 16));
                builder.append(HEX_DIGITS.charAt(b % 16));
                if (iByte >= cBytes) {
                    return;
                }
        } 
		
	}
	
	public static synchronized String encodePadded(byte[] bytes, int length){
		  		encode(bytes);
		        int numZeros = (length*2) - encoded.length();
		        while(numZeros-- > 0) { 
		            encoded.insert(0, "0");
		        }
		        return encoded.toString();
	}

}
