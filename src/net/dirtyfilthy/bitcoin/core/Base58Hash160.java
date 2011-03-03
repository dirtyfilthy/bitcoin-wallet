package net.dirtyfilthy.bitcoin.core;

import java.security.interfaces.ECPublicKey;
import java.text.ParseException;

import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.Base58;
import net.dirtyfilthy.bitcoin.util.HashTools;
import net.dirtyfilthy.bitcoin.util.KeyTools;

 
public class Base58Hash160 {
	private byte[] hash160;
	private String hash;
	public Base58Hash160(ECPublicKey k){
		byte[] encoded=KeyTools.encodePublicKey(k);
		hash160=HashTools.ripemd160(HashTools.sha256(encoded));
		byte[] toEncode=new byte[hash160.length+1];
		toEncode[0]=ProtocolVersion.addressVersion();
		System.arraycopy(hash160, 0, toEncode, 1, hash160.length);
		hash=Base58.encodeCheck(toEncode);
	}
	
	public Base58Hash160(String address) throws ParseException{
		byte[] decoded=Base58.decodeCheck(address);
		hash=address;
		hash160=new byte[decoded.length-1];
		System.arraycopy(decoded, 1, hash160, 0, hash160.length);
	}
	
	public byte[] hash160(){
		return hash160;
	}


	public String toString(){
		return hash;
	}
		
}
	
	


