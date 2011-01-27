package net.dirtyfilthy.bitcoin.test;

import java.io.ByteArrayInputStream; 
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.UnknownHostException;
import net.dirtyfilthy.bitcoin.core.Address;
import net.dirtyfilthy.bitcoin.protocol.*;
import android.test.AndroidTestCase;
public class ProtocolTest extends AndroidTestCase {
	
	private static void printBytes(
	        byte[] bytes
	        )
	    {
	        int cBytes = bytes.length;
	        int iByte = 0;

	        for (;;) {
	            for (int i = 0; i < 8; i++) {
	                String hex = Integer.toHexString(bytes[iByte++] & 0xff);
	                if (hex.length() == 1) {
	                    hex = "0" + hex;
	                }

	                System.out.print("0x" + hex + " ");
	                if (iByte >= cBytes) {
	                    System.out.println();
	                    return;
	                }
	            }
	            System.out.println();
	        } 
	    }
	
	public void setUp(){
		ProtocolVersion.useTestNet(false);
	}

	public void testAddress(){
		Address remoteAddress;
		try {
			remoteAddress = new Address("192.168.2.2",(short) 8333);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		byte[] bytes=remoteAddress.toByteArray(false);
		assertEquals(bytes.length,26);
	}
	
	public void testVersionPacket() throws StreamCorruptedException, IOException{
		Address remoteAddress;
		try {
			remoteAddress = new Address("192.168.2.2",(short) 8333);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		VersionPacket versionPacket=new VersionPacket();
		versionPacket.setRemoteAddress(remoteAddress);
		versionPacket.setFromAddress(remoteAddress);
		versionPacket.setNonce(666);
		versionPacket.setStartingHeight(666);
		
		assertEquals(versionPacket.getVersion(), 31800);
		byte[] versionContents=versionPacket.create();
		int incoming[] = {
				0xf9, 0xbe, 0xb4, 0xd9, 0x76, 0x65, 0x72, 0x73, 
				0x69, 0x6f, 0x6e, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x55, 0x00, 0x00, 0x00, 0x44, 0x7a, 0x00, 0x00, 
				0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			    0x84, 0x57, 0x14, 0x4d, 0x00, 0x00, 0x00, 0x00, 
				0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0xff, 0xff, 0x6f, 0x45, 0xf5, 0xd5, 
				0xa4, 0x5b, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0xff, 0xff, 0xd8, 0x1b, 
				0xab, 0xa5, 0x20, 0x8d, 0x43, 0x98, 0xe4, 0x7c, 
				0x8a, 0xce, 0x44, 0xfe, 0x00, 0x61, 0x83, 0x01, 
				0x00 };
		byte[] incomingBytes=new byte[incoming.length];
		for(int i=0;i<incoming.length;i++){
			incomingBytes[i]=(byte) incoming[i];
		}
		ByteArrayInputStream byteStream=new ByteArrayInputStream(incomingBytes);
		DataInputStream in=new DataInputStream(byteStream);
		VersionPacket v=new VersionPacket();
		v.readExternal(in);
		printBytes(v.create());
		System.out.println(v.getVersion());
		System.out.println(v.getCommand());
	}
	
}