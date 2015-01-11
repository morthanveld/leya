package com.mygdx.game;

import com.badlogic.gdx.utils.Array;

public class Wave 
{
	private int count = 0;
	private Array<SpawnLocation> spawnLocations = null;

	public Wave(int count)
	{
		this.count = count;
		this.spawnLocations = new Array<SpawnLocation>();
	}
	
	public void start()
	{
		for (SpawnLocation sl : spawnLocations)
		{
			sl.spawn(count / spawnLocations.size, 10);
		}
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
	
	public boolean isWaveFinished()
	{
		for (SpawnLocation sl : spawnLocations)
		{
			if (sl.remainingSpawns() > 0)
			{
				return false;
			}
		}
		
		return true;

	}
}
