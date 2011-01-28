package net.dirtyfilthy.bitcoin.test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.dirtyfilthy.bitcoin.core.Tx;
import net.dirtyfilthy.bitcoin.core.TxIn;
import net.dirtyfilthy.bitcoin.core.TxOut;
import net.dirtyfilthy.bouncycastle.util.encoders.Hex;
import android.test.AndroidTestCase;

public class TxTest extends AndroidTestCase {
	private byte[] rawTx;
	private DataInputStream in;
	public void setUp(){
		rawTx=Hex.decode("010000000330f3701f9bc464552f70495791040817ce777ad5ede16e529fcd0c0e94915694000000008c493046022100f5746b0b254f5a37e75251459c7a23b6dfcb868ac7467edd9a6fdd1d969871be02210088948aea29b69161ca341c49c02686a81d8cbb73940f917fa0ed7154686d3e5b01410447d490561f396c8a9efc14486bc198884ba18379bcac2e0be2d8525134ab742f301a9aca36606e5d29aa238a9e2993003150423df6924563642d4afe9bf4fe28ffffffff72142bf7686ce92c6de5b73365bfb9d59bb60c2c80982d5958c1e6a3b08ea689000000004a493046022100bce43ad3acbc79b0247e54c8c91eac1cf9037505000e01d1fd811854d85bc21a022100992a6f6f2feb6f62d3706f3b9aaab88d9f1132956a1dffa926cd556ed55360df01ffffffffd28128bbb6207c1c3d0a630cc619dc7e7bea56ac19a1dab127c62c78fa1b632c00000000494830450220209757368161537708fd29d89bb1e9d648007949ecfded789b51a96324cb6518022100cd0f7c30213916482b6e166d8a4f2b981f777eb184cd8a495f1b3d3690fbbf2d01ffffffff0100a6f75f020000001976a9149e35d93c7792bdcaad5697ddebf04353d9a5e19688ac00000000");
		in=new DataInputStream(new ByteArrayInputStream(rawTx));
	}
	
	public void testParse() throws IOException{
		Tx tx=new Tx(in);
		TxIn[] txIn=tx.getTxInputs();
		TxOut[] txOut=tx.getTxOutputs();
		assertEquals(3,txIn.length);
		assertEquals(1,txOut.length);
	}

}
