package net.dirtyfilthy.bitcoin.protocol;

import java.io.IOException;
import java.io.DataInputStream;


public class ReplyPacket extends Packet {
	
	private int replyCode;
	static public final int SUCCESS=0;
	static public final int ERROR=1;
	static public final int DENIED=2;
	public ReplyPacket() {
		super();
		command="reply";
		setReplyCode(DENIED);
		
	}

	public ReplyPacket(int ver) {
		super(ver,"reply");
		setReplyCode(DENIED);
	}

	public void setReplyCode(int replyCode) {
		this.replyCode = replyCode;
	}

	public int getReplyCode() {
		return replyCode;
	}
	
	public byte[] create(){
		dataBuffer.putInt(replyCode);
		return toByteArray();
	}
	
	public void readData(DataInputStream in) throws IOException{
		this.setReplyCode(Integer.reverseBytes(in.readInt()));
	}
	

}
