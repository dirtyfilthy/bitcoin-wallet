package net.dirtyfilthy.bitcoin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.dirtyfilthy.bouncycastle.crypto.digests.RIPEMD160Digest;
import net.dirtyfilthy.bouncycastle.jce.provider.JDKMessageDigest;

public class HashTools {
	public static MessageDigest sha256digester;
	public static MessageDigest sha1digester;
	public static MessageDigest ripemd160digester;
	

	public static byte[] sha256(byte[] toHash){
		try {
			if(sha256digester==null){
				sha256digester = MessageDigest.getInstance("SHA256");
			}
		} catch (NoSuchAlgorithmException e) {

			try {
				if(sha256digester==null){
					sha256digester = MessageDigest.getInstance("SHA-256");
				}
			} catch (NoSuchAlgorithmException e1) {
				throw new RuntimeException("Can't find SHA-256 message digest algorithm",e1);
			}
		}
		return sha256digester.digest(toHash);
	}

	public static byte[] sha1(byte[] toHash){
		try {
			if(sha1digester==null){
				sha1digester = MessageDigest.getInstance("SHA1");
			}
		} catch (NoSuchAlgorithmException e) {

			try {
				if(sha1digester==null){
					sha1digester = MessageDigest.getInstance("SHA-1");
				}
			} catch (NoSuchAlgorithmException e1) {
				throw new RuntimeException("Can't find SHA-1 message digest algorithm",e1);
			}
		}
		return sha1digester.digest(toHash);
	}

	public static byte[] doubleSha256(byte[] toHash){
		return sha256(sha256(toHash));
	}

	public static byte[] hash160(byte[] toHash){
		return ripemd160(sha256(toHash));
	}

	public static byte[] ripemd160(byte[] toHash){
		if(ripemd160digester==null){	
			ripemd160digester = new  JDKMessageDigest.RIPEMD160();
		}
		return ripemd160digester.digest(toHash);
	}

	public static byte[] reverseByteArray(byte[] a) {
		byte[] b=a.clone();
		int left  = 0;          // index of leftmost element
		int right = b.length-1; // index of rightmost element

		while (left < right) {
			// exchange the left and right elements, the old xor byte swap
			b[left]  = (byte) (b[left] ^ b[right]); 
			b[right] = (byte) (b[left] ^ b[right]); 
			b[left]  = (byte) (b[left] ^ b[right]); 

			// move the bounds toward the center
			left++;
			right--;
		}
		return b;
	}

}
