package net.dirtyfilthy.bitcoin.test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Address;
import net.dirtyfilthy.bitcoin.protocol.IrcBootStrap;
import net.dirtyfilthy.bitcoin.protocol.ProtocolVersion;
import android.test.AndroidTestCase;

public class IrcBootStrapTest extends AndroidTestCase {
	public void testGetAddresses() throws UnknownHostException, IOException, InterruptedException{
			ProtocolVersion.useTestNet(true);
			IrcBootStrap bootstrap=new IrcBootStrap(ProtocolVersion.ircHost(),ProtocolVersion.ircPort(), ProtocolVersion.ircChannel());
			Vector<Address> addresses=bootstrap.getAddresses();
			assertTrue("Didn't find addresses :(", addresses.size()>0);
	}
}
