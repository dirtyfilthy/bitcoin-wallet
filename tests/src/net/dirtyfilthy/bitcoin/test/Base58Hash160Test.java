package net.dirtyfilthy.bitcoin.test;

import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Arrays;




import net.dirtyfilthy.bitcoin.core.Base58Hash160;
import net.dirtyfilthy.bitcoin.util.KeyTools;
import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;

import android.test.AndroidTestCase;

public class Base58Hash160Test extends AndroidTestCase {
	


	public void testHash(){
		// actual public key from wallet
		String derKey="3056301006072a8648ce3d020106052b8104000a03420004fcf4e4a282c8ebe9de52575bd48d5bbd0ddd300d8aaee800132e67dd1dfa2ad294569c6736e540d0df7254fca341c210434244067bb27790d2fcfdd68b7f7ab7"; 
		ECPublicKey pubKey=KeyTools.decodeDerPublicKey(Hex.decode(derKey));
		String target="1E7hzY5DSAhX4BMTrp3VteCZzvCRtFK1ks";
		Base58Hash160 address=new Base58Hash160(pubKey);
		assertEquals(target,address.toString());
	}
	
	public void testHash160() throws ParseException{
		String address="1E7hzY5DSAhX4BMTrp3VteCZzvCRtFK1ks";
		byte[] hash160=new Base58Hash160(address).hash160();
		String derKey="3056301006072a8648ce3d020106052b8104000a03420004fcf4e4a282c8ebe9de52575bd48d5bbd0ddd300d8aaee800132e67dd1dfa2ad294569c6736e540d0df7254fca341c210434244067bb27790d2fcfdd68b7f7ab7"; 
		ECPublicKey pubKey=KeyTools.decodeDerPublicKey(Hex.decode(derKey));
		Base58Hash160 address2=new Base58Hash160(pubKey);
		System.out.println(MyHex.encode(hash160));
		System.out.println(MyHex.encode(address2.hash160()));
		assertTrue(Arrays.equals(hash160,address2.hash160()));
		address="1N67d3fXENpegeVCRTgbhkWCNKEYqYjjja";
		assertTrue("Not generating correct hash160",Arrays.equals(new Base58Hash160(address).hash160(),Hex.decode("e7523fa46e76343cc2d9bbd2882a7d80961d18a1")));
	}
}