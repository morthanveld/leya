package com.mygdx.game;

import java.nio.ByteBuffer;

public class Packet 
{	
	public static final byte IO_KEYBOARD = 0x1;
	public static final byte IO_MOUSE = 0x2;
	public static final byte POSITION = 0x3;
	public static final byte PROJECTILE = 0x4;
	
	private byte[] data = null;
	
	public Packet(byte[] data)
	{
		this.data = data.clone();
	}
	
	public byte[] getData()
	{
		return this.data;
	}
	
	/*
	public static byte[] floatToBytes(float value)
	{
		int bits = Float.floatToIntBits(value);
		byte[] bytes = new byte[4];
		bytes[0] = (byte)(bits & 0xff);
		bytes[1] = (byte)((bits >> 8) & 0xff);
		bytes[2] = (byte)((bits >> 16) & 0xff);
		bytes[3] = (byte)((bits >> 24) & 0xff);
		
		return bytes;
	}
	*/
	
	public static byte [] floatToBytes (float value)
	{  
	     return ByteBuffer.allocate(4).putFloat(value).array();
	}
}
