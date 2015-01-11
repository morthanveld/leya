package com.mygdx.game;

import com.badlogic.gdx.utils.Array;

public class Wave 
{
	private Array<SpawnLocation> spawnLocations = null;

	public Wave()
	{
		this.spawnLocations = new Array<SpawnLocation>();
	}
	
	public void update(float dt)
	{
		for (SpawnLocation sl : spawnLocations)
		{
			sl.update(dt);
		}
	}
	
	public void addSpawnLocation(SpawnLocation spawnLocation)
	{
		this.spawnLocations.add(spawnLocation);
	}
}
