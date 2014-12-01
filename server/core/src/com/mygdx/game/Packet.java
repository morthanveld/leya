package com.mygdx.game;

public class Packet 
{
	private byte[] data = null;
	
	public Packet(byte[] data)
	{
		this.data = data;  
	}
	
	public byte[] getData()
	{
		return this.data;
	}
}
