package net.dirtyfilthy.bitcoin.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.InvalidKeySpecException;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.dirtyfilthy.bitcoin.core.Base58Hash160;
import net.dirtyfilthy.bitcoin.wallet.Wallet;
import net.dirtyfilthy.bouncycastle.jce.ECNamedCurveTable;
import net.dirtyfilthy.bouncycastle.jce.ECPointUtil;
import net.dirtyfilthy.bouncycastle.jce.provider.asymmetric.ec.EC5Util;
import net.dirtyfilthy.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import net.dirtyfilthy.bouncycastle.jce.spec.ECParameterSpec;
import net.dirtyfilthy.bouncycastle.math.ec.ECCurve;

public class KeyTools {
	
	// encodes a public key into the form [0x04][32 bytes 'x' value][32 bytes 'y' value]
	
	public static byte[] encodePublicKey(ECPublicKey key){
		net.dirtyfilthy.bouncycastle.math.ec.ECPoint p=EC5Util.convertPoint(key.getParams(), key.getW(), false);
		return p.getEncoded();
	}
	
	// decodes a raw public key in the form [0x04][32 bytes 'x' value][32 bytes 'y' value]
	
	public static ECPublicKey decodePublicKey(byte[] encoded){
		ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("ECDSA", "DFBC");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		}
		ECCurve curve = params.getCurve();
		java.security.spec.EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, params.getSeed());
	    java.security.spec.ECPoint point=ECPointUtil.decodePoint(ellipticCurve, encoded);
	    java.security.spec.ECParameterSpec params2=EC5Util.convertSpec(ellipticCurve, params);
	    java.security.spec.ECPublicKeySpec keySpec = new java.security.spec.ECPublicKeySpec(point,params2);
	    try {
			return (ECPublicKey) fact.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
		
	}
	

	public static byte[] signData(ECPrivateKey key,byte data[]){
		Signature s;
		try {
			s = Signature.getInstance("ECDSA", "DFBC");
			s.initSign(key);
			s.update(data);
			return s.sign();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
		
	}
	

	public static KeyPair generateKeyPair() {
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("ECDSA", "DFBC");
			generator.initialize(ecSpec, new SecureRandom());
			KeyPair pair = generator.generateKeyPair();
			ContentValues initialValues = new ContentValues();
			return pair;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
		
	}

	public static boolean verifySignedData(ECPublicKey key,byte data[], byte sig[]){
		Signature s; 
		try {
		s= Signature.getInstance("ECDSA", "DFBC");
		s.initVerify(key);
		s.update(data);
		return s.verify(sig);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}

}
