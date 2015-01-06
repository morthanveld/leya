package com.mygdx.game;

public class Event 
{
	public static final byte EVENT_ENTITY_DESTROY = 0x1;
	
	private byte type = 0;
	private byte entityId;
	
	public byte getType()
	{
		return this.type;
	}
	
	public void createEntityDestroy(byte entityId)
	{
		this.type = EVENT_ENTITY_DESTROY;
		this.entityId = entityId;
	}
	
	public byte getEntityId()
	{
		return entityId;
	}
}
