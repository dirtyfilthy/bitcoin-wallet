package net.dirtyfilthy.bitcoin.wallet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;


import net.dirtyfilthy.bouncycastle.jce.provider.BouncyCastleProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class Wallet {
	private KeyRing keyRing;
	public static final String SECURITY_PROVIDER="DFBC";
	private SQLiteDatabase db;
	private Context context;
	public static final int DATABASE_VERSION=1;
	public Wallet(Context c, String db_filepath, String password) throws FileNotFoundException, KeyStoreException, InvalidPasswordException{
		if(Security.getProvider(SECURITY_PROVIDER)==null){
			Security.addProvider(new BouncyCastleProvider());
		}
		this.context=c;
		db=new OpenHelper(context,db_filepath).getWritableDatabase();
		
		try {
			keyRing=new KeyRing(c, db, password);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e);
		} catch (NoSuchProviderException e) {
			throw new KeyStoreException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new KeyStoreException(e);
		} catch (KeyStoreException e) {
			throw new KeyStoreException(e);
		} catch (CertificateException e) {
			throw new InvalidPasswordException(e);
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			throw new KeyStoreException(e);
		}	
	}
	

	private static class OpenHelper extends SQLiteOpenHelper{
		OpenHelper(Context context,String filename) {
			super(context, filename , null, 1);

		}
		
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE keys (id INTEGER PRIMARY KEY AUTOINCREMENT, base58hash160 VARCHAR, public_key BLOB, private_key BLOB);");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("");
			
		}
	}
	
	

}
