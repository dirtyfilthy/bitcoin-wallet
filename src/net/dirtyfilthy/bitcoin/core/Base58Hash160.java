package net.dirtyfilthy.bitcoin.core;



import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import net.dirtyfilthy.bitcoin.util.Base58;
import net.dirtyfilthy.bitcoin.util.QuickHash;
import net.dirtyfilthy.bouncycastle.jce.interfaces.ECPublicKey;

public class Base58Hash160 {
	
	private String hash;
	public Base58Hash160(ECPublicKey k){
		byte[] encoded=k.getEncoded();
		byte[] ripemd160=QuickHash.ripemd160(QuickHash.sha256(encoded));
		byte[] toEncode=new byte[ripemd160.length+1];
		toEncode[0]=ProtocolVersion.addressVersion();
		System.arraycopy(ripemd160, 0, toEncode, 1, ripemd160.length);
		hash=Base58.encodeCheck(toEncode);
	}
	
	public String toString(){
		return hash;
	}
		
}
	
	


