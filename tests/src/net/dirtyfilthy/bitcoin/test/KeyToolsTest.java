package net.dirtyfilthy.bitcoin.test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Arrays;


import net.dirtyfilthy.bitcoin.util.Base58;
import net.dirtyfilthy.bitcoin.util.KeyTools;
import net.dirtyfilthy.bouncycastle.jce.provider.BouncyCastleProvider;
import android.test.AndroidTestCase;

public class KeyToolsTest extends AndroidTestCase {
	
	public void setUp(){
		if(Security.getProvider("DFBC")==null){
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	
	public void testPublicKeyDecode() throws ParseException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException{
		byte[] encodedKey=Base58.decode("Nb3otRtEZ3QF1uuA2PMnyzLjtNwUp3pmbbvmAFwsqdw2syJQekgKpfjbzSAFS3TrDveRFr65WHW9iUpCRkrEdkQz");
		assertEquals(65,encodedKey.length);
		ECPublicKey key=KeyTools.decodePublicKey(encodedKey);
	}
	
	public void testPublicKeyDecodeEncode() throws ParseException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException{
		byte[] encodedKey=Base58.decode("Nb3otRtEZ3QF1uuA2PMnyzLjtNwUp3pmbbvmAFwsqdw2syJQekgKpfjbzSAFS3TrDveRFr65WHW9iUpCRkrEdkQz");
		assertEquals(65,encodedKey.length);
		ECPublicKey key=KeyTools.decodePublicKey(encodedKey);
		byte[] encodedKey2=KeyTools.encodePublicKey(key);
		assertTrue("Does not reencode to same byte array", Arrays.equals(encodedKey, encodedKey2));
	}
	
	public void testGenerateKeyPair(){
		KeyPair pair=KeyTools.generateKeyPair();
	}

}
