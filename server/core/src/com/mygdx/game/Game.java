package com.mygdx.game;

public class Game
{
	private int numberOfWaves = 0;
	private Wave wave = null;
	
	static final int STATE_LOBBY = 0x1;
	static final int STATE_START = 0x2;
	static final int STATE_RUNNING = 0x3;
	static final int STATE_END = 0x4;
	
	private int state = 0;
	
	public Game()
	{
	}
	
	public void update(float dt)
	{
		System.out.println("state: " + this.state);
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
