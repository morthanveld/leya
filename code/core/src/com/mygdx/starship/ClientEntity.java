package com.mygdx.starship;

public class ClientEntity 
{
	private int id = 0;
	private int type = 0;
	
	public ClientEntity(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public int getType() 
	{
		return type;
	}

	public void setType(int type) 
	{
		this.type = type;
	}
}
