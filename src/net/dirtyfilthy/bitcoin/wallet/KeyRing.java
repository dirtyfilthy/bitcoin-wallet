package net.dirtyfilthy.bitcoin.wallet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.dirtyfilthy.bitcoin.core.Base58Hash160;
import net.dirtyfilthy.bouncycastle.jce.ECNamedCurveTable;
import net.dirtyfilthy.bouncycastle.jce.interfaces.ECPublicKey;
import net.dirtyfilthy.bouncycastle.jce.spec.ECParameterSpec;

public class KeyRing {
	private KeyPairGenerator generator;
	private SQLiteDatabase db;
	private String password;
	private Context context;
	public KeyRing(Context c, SQLiteDatabase db, String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, FileNotFoundException, IOException{
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
		this.db=db;
		this.context=c;
		KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDSA", Wallet.SECURITY_PROVIDER);
		generator.initialize(ecSpec, new SecureRandom());
	}
	

	KeyPair generateKeyPair() {
		KeyPair pair = generator.generateKeyPair();
		String hash=new Base58Hash160((java.security.interfaces.ECPublicKey) pair.getPublic()).toString();
		ContentValues initialValues = new ContentValues();
		initialValues.put("base58hash160",hash);
		initialValues.put("public_key", pair.getPublic().getEncoded());
		initialValues.put("private_key", pair.getPrivate().getEncoded());
		db.insert("keys", null, initialValues);
		return pair;
	}

	

}
