package net.dirtyfilthy.bitcoin.protocol;
import java.nio.*;
import java.nio.charset.*;
import java.util.Arrays;
import java.io.*;
import java.math.BigInteger;

import net.dirtyfilthy.bitcoin.core.ByteArrayable;
import net.dirtyfilthy.bitcoin.util.QuickHash;

public class Packet  implements ByteArrayable {
	
	
	public static final int SER_NETWORK=1 << 0;
	public static final int SER_DISK=1 << 1;
	public static final int SER_GETHASH=1 << 2;
	public static final int SER_SKIPSIG=1 << 16;
	public static final int SER_BLOCKHEADERONLY=1 << 17;
	
	public static final byte MAGIC[]={(byte) (0xf9 & 0xff),(byte) (0xbe & 0xff),(byte) (0xb4 & 0xff),(byte) (0xd9 & 0xff)};
	public static final int USHORT_MAX=Short.MAX_VALUE << 1;
	public static final long UINT_MAX=Integer.MAX_VALUE << 1;
	public static final BigInteger BIGINT_LONG_SIGNED_MAX=BigInteger.valueOf(Long.MAX_VALUE);
	public static final int HEADER_LENGTH=24;
	protected int packetType; 
	protected String command;
	protected long version;
	ByteBuffer dataBuffer;
	protected long dataSizeFromHeader=0;
	protected byte[] checksumFromHeader={0,0,0,0};
	protected 
	
	Packet(){
		dataBuffer=ByteBuffer.allocate(2000);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		setVersion(ProtocolVersion.version());
		packetType=0;
	}
	

	Packet(long version2){
		dataBuffer=ByteBuffer.allocate(2000);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		setVersion(version2);
		packetType=0;
	}
	
	Packet(long version2, String command){
		dataBuffer=ByteBuffer.allocate(2000);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		setVersion(version2);
		setCommand(command);
		packetType=0;
	}
	
	private String headerToString(){
		String header="";
		header+="command '"+command+"'";
		header+=" dataSize "+dataSize();
		return header;
		
		
	}
	
	public String toString(){
		return headerToString();
	}
	
	public void setPacketType(int t){
		this.packetType=t;
	}
	
	public int getPacketType(){
		return this.packetType;
	}
	
	
	
	
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	
	public static long readUnsignedVarInt(DataInputStream in) throws IOException{
		int value=in.readUnsignedByte();
		long i=0;
		if(value>=0 && value<253){
			i=value;
		}
		else if(value==253){
			i=(((int) Short.reverseBytes(in.readShort())) & 0xffff);
		}
		else if(value==254){
			i=Integer.reverseBytes(in.readInt()) & (long) 0xffffffffL;
		}
		else if(value==255){
			i=Long.reverseBytes(in.readLong());
		}
		return i;
	}
	
	public static byte[] readVariableField(DataInputStream in) throws IOException{
		long size=readUnsignedVarInt(in);
		System.out.println("reading var field of size "+size);
		byte[] field=new byte[(int) size];
		in.readFully(field);
		return field;
	}
	
	public String readVariableString(DataInputStream in) throws IOException{
		byte[] raw=readVariableField(in);
		return new String(raw,"ISO-8859-1");
	}
	
	public static byte[] createUnsignedVarInt(int value){
		ByteBuffer buffer=ByteBuffer.allocate(9);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		if(value>=0 && value<253){
			buffer.put((byte) ((short) value & 0xff));
		}
		else if(value>0 && value<USHORT_MAX) {
			buffer.put((byte) ((short) 253 & 0xff));
			buffer.putShort ((short)(value & 0xffff));
		}
		else if(value>0 && value<UINT_MAX) {
			buffer.put((byte) ((short) 254 & 0xff));
			buffer.putInt((int)(value & 0xffffffff));
		}
		else{
			buffer.put((byte) ((short) 255 & 0xff));
			buffer.putLong((long) (value));
		}
		byte[] ret=new byte[buffer.position()];
		ByteBuffer slicedBuffer=(ByteBuffer) buffer.duplicate();
		slicedBuffer.rewind();
		slicedBuffer.limit(buffer.position());
		slicedBuffer.get(ret);
		return ret;
	}
	
	public void writeUnsignedVarInt(int value){
		dataBuffer.put(createUnsignedVarInt(value));
	}
		

	public void writeVariableField(byte[] field){
		int size=field.length;
		writeUnsignedVarInt(size);
		dataBuffer.put(field);
	}
	
	public long dataSize(){
		if(dataBuffer.position()==0){
			return getDataSizeFromHeader();
		}
		return dataBuffer.position();
	}
	
	public byte[] checksum(){
		return checksum(dataContents());
	}
	
	public byte[] checksum(byte[] toChecksum){
		byte[] checksum={0,0,0,0};
		if (version>=209){
			byte[] digest=QuickHash.doubleSha256(toChecksum);
			for(int i=0;i<4;i++){
				checksum[i]=digest[i];
			}
		}
		return checksum;
	}
	
	private ByteBuffer stringToIso8859(String string) {
		Charset charset = Charset.forName("ISO-8859-1");
		CharsetEncoder encoder = charset.newEncoder();
		ByteBuffer bbuf = null;
		try{
			bbuf = encoder.encode(CharBuffer.wrap(string));
		}
		catch(CharacterCodingException e){
			throw new RuntimeException("Error encoding the command string",e); 
		}
		return bbuf;
	}
	
	protected void writeVariableStringField(String s){
		if(s==""){
			writeVariableField(new byte[0]);
		}
		else{
			byte[] conv=stringToIso8859(s).array();
			byte[] full=new byte[conv.length+1];
		
			full[conv.length]=0; // null terminate
			System.arraycopy(conv, 0, full, 0, conv.length);
			writeVariableField(full);
		}
	}
	

	protected void writeFixedStringField(String s, int len){
		int truncatedLength=s.length()<len ? s.length() : len;
		ByteBuffer truncated_command=stringToIso8859(s.substring(0,truncatedLength));
		dataBuffer.put(truncated_command.array());
		for(int i=0;i<len-truncatedLength;i++){
			dataBuffer.put((byte) 0x00);
		}
	}
	

	protected void writeFixedStringField(ByteBuffer b, String s, int len){
		int truncatedLength=s.length()<len ? s.length() : len;
		ByteBuffer truncated_command=stringToIso8859(s.substring(0,truncatedLength));
		b.put(truncated_command.array());
		for(int i=0;i<len-truncatedLength;i++){
			b.put((byte) 0x00);
		}
	}
	

	
	
	private byte[]  header(){
		
		ByteBuffer buffer=ByteBuffer.allocate(HEADER_LENGTH);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(ProtocolVersion.magic());
		writeFixedStringField(buffer, command,12);
		buffer.putInt((int) this.dataSize());
		if(shouldChecksum()){
			buffer.put(this.checksum());
		}
		byte[] headerContents=new byte[buffer.position()];
		ByteBuffer slicedBuffer=(ByteBuffer) buffer.duplicate();
		slicedBuffer.rewind();
		slicedBuffer.limit(buffer.position());
		slicedBuffer.get(headerContents);
		return headerContents;
	}
	
	
	public PacketType packetType(){
		return PacketType.valueOf(command.toUpperCase());
	}
	
	public byte[] dataContents(){
		byte[] dataContents=new byte[dataBuffer.position()];
		ByteBuffer slicedBuffer=(ByteBuffer) dataBuffer.duplicate();
		slicedBuffer.rewind();
		slicedBuffer.limit(dataBuffer.position());
		slicedBuffer.get(dataContents);
		return dataContents;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
	
	public byte[] toByteArray(){
		byte[] array;
		byte[] headerContents=header();
		byte[] dataContents=dataContents();
		array= new byte[headerContents.length+dataContents.length];
		System.arraycopy(headerContents, 0, array, 0, headerContents.length);
		System.arraycopy(dataContents, 0, array, headerContents.length, dataContents.length);
		return array;
	}
	
	public byte[] create(){
		return toByteArray();
	}
	
	public void writeExternal(DataOutput out) throws IOException {
		create();
		out.write(toByteArray());
	}
	
	protected void readData(DataInputStream in) throws IOException {
	}
	
	protected void readHeader(DataInputStream in) throws IOException, MalformedPacketException {
		System.out.println("reading header");
		byte[] magic=new byte[4];
		byte[] cmd=new byte[12];
		in.readFully(magic);
		
		if(!Arrays.equals(magic,ProtocolVersion.magic())){
			throw new MalformedPacketException("Incorrect magic!");
		}
		in.readFully(cmd);
		command=new String(cmd,"ISO-8859-1");
		int nullPos = command.indexOf(0);  
		command = (nullPos < 0) ? command : command.substring(0, nullPos);  
		System.out.println("command '"+command+"'");
		dataSizeFromHeader=(long) Integer.reverseBytes(in.readInt()) & 0xffffffffL;
		System.out.println("datasize "+dataSizeFromHeader);
		if(shouldChecksum()){
			System.out.println("readingChecksum");
			in.readFully(checksumFromHeader);
		}
		System.out.print("checksum: ");
		System.out.println(checksumFromHeader[0]);
	}
	
	public long getDataSizeFromHeader(){
		return this.dataSizeFromHeader;
	}
	
	private boolean shouldChecksum(){
		return (version>=209 
				&& packetType()!=PacketType.VERSION 
				&& (this.dataSize()>0 || packetType()==PacketType.GETADDR));
	}

	
	public void readExternal(DataInputStream in) throws IOException {
		
		readHeader(in);
		byte rawData[]=new byte[(int) this.dataSizeFromHeader];
		in.readFully(rawData);
		
		// VALIDATE CHECKSUM: version packets don't contain checksums, neither do packets with versions < 209
		
		if(version>=209 
				&& packetType()!=PacketType.VERSION 
				&& (this.dataSize()>0 || packetType()==PacketType.GETADDR) 
			&& !Arrays.equals(checksumFromHeader,checksum(rawData))){
			throw new MalformedPacketException("Incorrect checksum!");
		}
		DataInputStream in2=new DataInputStream(new ByteArrayInputStream(rawData));
		readData(in2);
	}
	
	
/*	public void writeUnsignedData(BigInteger value){
		byte byteArray[];
		if value.bitLength()!=8
		
		if(value.compareTo(BIGINT_LONG_SIGNED_MAX)==-1){
			writeUnsignedData(value.longValue());
		}
		else{
			byteArray=value.toByteArray();
			data.put((byte) ((short) 255 & 0xff));
			for(int i=7;i>=0;i--){
				data.put(byteArray[i]);
			}
		}
	}
	*/

}
