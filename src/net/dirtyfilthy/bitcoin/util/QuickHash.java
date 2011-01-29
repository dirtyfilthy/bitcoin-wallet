package net.dirtyfilthy.bitcoin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.dirtyfilthy.bouncycastle.crypto.digests.RIPEMD160Digest;
import net.dirtyfilthy.bouncycastle.jce.provider.JDKMessageDigest;

public class QuickHash {
	
	public static byte[] sha256(byte[] toHash){
		MessageDigest digester;
		try {
			digester = MessageDigest.getInstance("SHA256");
		} catch (NoSuchAlgorithmException e) {
		
			try {
				digester = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e1) {
				throw new RuntimeException("Can't find SHA-256 message digest algorithm",e1);
			}
	}
	return digester.digest(toHash);
	}
	
	public static byte[] sha1(byte[] toHash){
		MessageDigest digester;
		try {
			digester = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
		
			try {
				digester = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e1) {
				throw new RuntimeException("Can't find SHA-1 message digest algorithm",e1);
			}
	}
	return digester.digest(toHash);
	}
	
	public static byte[] doubleSha256(byte[] toHash){
		return sha256(sha256(toHash));
	}
	
	public static byte[] hash160(byte[] toHash){
		return ripemd160(sha256(toHash));
	}
	
	public static byte[] ripemd160(byte[] toHash){
		MessageDigest digester;;
		digester = new  JDKMessageDigest.RIPEMD160(); 
		return digester.digest(toHash);
	}
	
	public static byte[] reverseByteArray(byte[] a) {
		   byte[] b=a.clone();
		   int left  = 0;          // index of leftmost element
		   int right = b.length-1; // index of rightmost element
		  
		   while (left < right) {
		      // exchange the left and right elements
		      byte temp = b[left]; 
		      b[left]  = b[right]; 
		      b[right] = temp;
		     
		      // move the bounds toward the center
		      left++;
		      right--;
		   }
		   return b;
		}
	
}
