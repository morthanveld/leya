package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Game
{
	private int currentWave = 0;
	private Array<Wave> waves = null;
	
	static final int STATE_LOBBY = 0x1;
	static final int STATE_START = 0x2;
	static final int STATE_RUNNING = 0x3;
	static final int STATE_END = 0x4;
	
	private int state = 0;
	
	private StarshipServer server = null;
	
	public Game(StarshipServer server)
	{
		this.waves = new Array<Wave>();
		Wave w = new Wave(100);
		w.addSpawnLocation(new SpawnLocation(server, new Vector2(200.0f, 200.0f)));
		w.addSpawnLocation(new SpawnLocation(server, new Vector2(-300.0f, 300.0f)));
		this.waves.add(w);
	}
	
	public void update(float dt)
	{
		System.out.println("state: " + this.state);
		
		if (this.currentWave < this.waves.size)
		{
			Wave w = this.waves.get(this.currentWave);
			w.update(dt);
			
			if (w.isWaveFinished())
			{
				// Go to next wave.
				this.currentWave++;
			}

			
		}
	}
	
	public void nextState()
	{
		state++;
		if (state > 4)
		{
			state = this.STATE_END;
		}
	}

	public int getState()
	{
		return this.state;
	}
}
