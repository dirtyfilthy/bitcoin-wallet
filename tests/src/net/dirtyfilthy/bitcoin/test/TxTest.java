package net.dirtyfilthy.bitcoin.test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;



import net.dirtyfilthy.bitcoin.core.Tx;
import net.dirtyfilthy.bitcoin.core.TxIn;
import net.dirtyfilthy.bitcoin.core.TxOut;
import net.dirtyfilthy.bitcoin.util.MyHex;
import net.dirtyfilthy.bouncycastle.util.Arrays;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;
import android.test.AndroidTestCase;

public class TxTest extends AndroidTestCase {
	private byte[] rawFromTx;
	private byte[] rawWrongFromTx;
	private byte[] rawToTx;
	
	private DataInputStream fromTxStream;
	private DataInputStream toTxStream;
	private DataInputStream wrongFromTxStream;
	public void setUp(){
		
		// http://blk.bitcoinwatch.com/b?h=103958
		rawToTx=    Hex.decode("010000000330f3701f9bc464552f70495791040817ce777ad5ede16e529fcd0c0e94915694000000008c493046022100f5746b0b254f5a37e75251459c7a23b6dfcb868ac7467edd9a6fdd1d969871be02210088948aea29b69161ca341c49c02686a81d8cbb73940f917fa0ed7154686d3e5b01410447d490561f396c8a9efc14486bc198884ba18379bcac2e0be2d8525134ab742f301a9aca36606e5d29aa238a9e2993003150423df6924563642d4afe9bf4fe28ffffffff72142bf7686ce92c6de5b73365bfb9d59bb60c2c80982d5958c1e6a3b08ea689000000004a493046022100bce43ad3acbc79b0247e54c8c91eac1cf9037505000e01d1fd811854d85bc21a022100992a6f6f2feb6f62d3706f3b9aaab88d9f1132956a1dffa926cd556ed55360df01ffffffffd28128bbb6207c1c3d0a630cc619dc7e7bea56ac19a1dab127c62c78fa1b632c00000000494830450220209757368161537708fd29d89bb1e9d648007949ecfded789b51a96324cb6518022100cd0f7c30213916482b6e166d8a4f2b981f777eb184cd8a495f1b3d3690fbbf2d01ffffffff0100a6f75f020000001976a9149e35d93c7792bdcaad5697ddebf04353d9a5e19688ac00000000");
		
		// http://blk.bitcoinwatch.com/b?h=103640
		rawFromTx=  Hex.decode("01000000020f7b7fb86d4cf646058e41d3b007183fdf79736ed19b2a7468abc5bd04b16e91000000008c493046022100b2ee39d2fcc2e5544a57c30f7b4e49cfb82222666d034fb90e22348e17e28e0f022100db91c3199cc7b41d4d7afce0ccb4ceb424b9476d51c06142583daf53ce0a9b66014104c32215a9093011bd3c41283ace3d002c666077b24a605b3cfc8f71019a0f43df66f389f3d9a62188a494b869dc7e5f9dffc98a76d3088a21e9b738ec9eba98cbffffffff97004125528f7b5ed33465caaae021c0b815f3e6a3707641d5a0bca43fc14949010000008a473044022033d02c2e896f1a1252488d534cfb08abf3e7ea90aba7ba6f57abf189cef1d837022005668d755013b0e59a2af5145f10efe62ea716d333268b0b5a3efbd82d1439be014104c32215a9093011bd3c41283ace3d002c666077b24a605b3cfc8f71019a0f43df66f389f3d9a62188a494b869dc7e5f9dffc98a76d3088a21e9b738ec9eba98cbffffffff0100c2eb0b000000001976a91402bf4b2889c6ada8190c252e70bde1a1909f961788ac00000000f9beb4d9db0000000100000028f07c0e98343b0642fff649b2c91eeecc5e085abfa63b790237000000000000d5c8fcc0de24eea6c0bd9e6a4a4cd3b60319c4fda67976985e8c89fba967ff49632c384dee8d031b46b18d580101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0b04ee8d031b0574b0050001ffffffff0100f2052a01000000434104a93238e0b0cb5ae22f5de0423905e6a26dc990660529c157f94318604110f0e6a14018c8a5f273e140b55ef95414d03b67bf874ae6");
		
		// this is exactly the same as FromTx with the scriptSig altered by one byte to be incorrect
		// rawWrongFromTx=Hex.decode("010000000330f3701f9bc464552f70495791040817ce777ad5ede16e529fcd0c0e94915694000000008c493046022100f5746b0b254f5a37e75251459c7a23b6dfcb868ac7467edd9a6fdd1d969871be02210088948aea29b69161ca341c49c02686a81d8cbb73940f917fa0ed7154686d3e5b01410447d490561f396c8a9efc14486bc198884ba18379bcac2e0be2d8525134ab742f301a9aca36606e5d29aa238a9e2993003150423df6924563642d4afe9bf4fe28ffffffff72142bf7686ce92c6de5b73365bfb9d59bb60c2c80982d5958c1e6a3b08ea689000000004a493046022100bce43ad3acbc79b0247e54c8c91eac1cf9037505000e01d1fd811854d85bc21a022100992a6f6f2feb6f62d3706f3b9aaab88d9f1132956a1dffa926cd556ed55360df01ffffffffd28128bbb6207c1c3d0a630cc619dc7e7bea56ac19a1dab127c62c78fa1b632c00000000494830450220209757368161537708fd29d89bb1e9d648007949ecfded789b51a96324cb6518022100cd0f7c30213916482b6e166d8a4f2b981f777eb184cd8a495f1b3d3690fbbf2d01ffffffff0100a6f75f020000001976a9149e35d93c7792bdcaad5697ddebf04353d9a5e19688ac00000000");

		
		fromTxStream =new DataInputStream(new ByteArrayInputStream(rawFromTx));
		toTxStream =new DataInputStream(new ByteArrayInputStream(rawToTx));
	//	wrongFromTxStream =new DataInputStream(new ByteArrayInputStream(rawWrongFromTx));
	}
	
	public void testParse() throws IOException{
		Tx toTx=new Tx(toTxStream);
		System.out.println("tx: "+MyHex.encode(toTx.toByteArray()));
		Vector<TxIn> txIn=toTx.getTxInputs();
		Vector<TxOut> txOut=toTx.getTxOutputs();
		assertEquals(3,txIn.size());
		assertEquals(1,txOut.size());
		Tx fromTx=new Tx(fromTxStream);
		txIn=fromTx.getTxInputs();
		txOut=fromTx.getTxOutputs();
		assertEquals(2,txIn.size());
		assertEquals(1,txOut.size());
	}
	
	public void testConstructFromOther() throws IOException{
		Tx tx=new Tx(toTxStream);
		Tx tx2=new Tx(tx);
		assertTrue("Hashes for tx are not equal",Arrays.areEqual(tx.hash(), tx2.hash()));
	}
	
	public void testVerifySignature() throws IOException{
		Tx fromTx=new Tx(fromTxStream);
		Tx toTx=new Tx(toTxStream);
		assertTrue("verifySignature returned false on legitimate signature", Tx.verifySignature(fromTx, toTx, 0, 0));
		
	}
	
}
