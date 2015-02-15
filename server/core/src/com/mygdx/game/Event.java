package com.mygdx.game;

public class Event 
{
	public static final int EVENT_ENTITY_DESTROY = 0x1;
	public static final int EVENT_PLAYER_READY = 0x2;
	
	private int type = 0;
	private int entityId;
	
	public int getType()
	{
		return this.type;
	}
	
	public void createEntityDestroy(int entityId)
	{
		this.type = EVENT_ENTITY_DESTROY;
		this.entityId = entityId;
	}
	
	public void createPlayerReady(int entityId)
	{
		this.type = EVENT_PLAYER_READY;
		this.entityId = entityId;
	}
	
	public int getEntityId()
	{
		return entityId;
	}
}
