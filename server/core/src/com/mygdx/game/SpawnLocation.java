package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class SpawnLocation 
{
	private int count = 0;
	private float interval = 0.0f;
	private Game game = null;
	private Vector2 position;
	private float timer = 0.0f;
	
	public SpawnLocation(Game game, Vector2 position)
	{
		this.game = game;
		this.position = position;
	}
	
	/**
	 * Update each frame.
	 */
	public void update(float dt)
	{
		// Reduce timer.
		this.timer -= dt;
		
		// Spawn an enemy if pool > 0 and interval reached.
		if (count > 0 && timer < 0.0f)
		{
			this.game.createEnemy(this.position);
			this.count--;
			this.timer = this.interval; 
		}
	}
	
	/**
	 * Spawns x amounts of ships with y seconds between each.
	 */
	public void spawn(int shipsToSpawn, float interval)
	{
		this.count = shipsToSpawn;
		this.interval = interval;
	}
	
	public int remainingSpawns()
	{
		return this.count;
	}
}
