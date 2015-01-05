package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WorldSingleton 
{
	private static WorldSingleton instance = null;
	private static World world = null;
	
	protected WorldSingleton() 
	{
		// Exists only to defeat instantiation.
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public static WorldSingleton getInstance() 
	{
		if(instance == null) 
		{
			instance = new WorldSingleton();
			world = new World(new Vector2(0.0f, 0.0f), true);
		}
		return instance;
	}
}