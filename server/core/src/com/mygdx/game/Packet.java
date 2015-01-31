package com.mygdx.game;

import java.nio.ByteBuffer;

public class Packet 
{	
	public static final byte IO_KEYBOARD = 0x1;
	public static final byte IO_MOUSE = 0x2;
	public static final byte POSITION = 0x3;
	public static final byte PROJECTILE = 0x4;
	public static final byte EVENT = 0x5;
	public static final byte WORLD = 0x6;
	
	private byte[] data = null;
	
	public Packet(byte[] data)
	{
		this.data = data.clone();
	}
	
	public byte[] getData()
	{
		return this.data;
	}
		
	public static byte [] floatToBytes (float value)
	{  
	     return ByteBuffer.allocate(4).putFloat(value).array();
	}
}
