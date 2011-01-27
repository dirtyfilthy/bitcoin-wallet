package net.dirtyfilthy.bitcoin.protocol;

import java.net.ProtocolException;

public class MalformedPacketException extends ProtocolException {

	public MalformedPacketException(String string) {
		super(string);
	}
	
	public MalformedPacketException(){
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 9162650754365036198L;

}
