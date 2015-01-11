package com.mygdx.starship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Event;

public class Lobby
{
	private Game game = null;
	private SpriteBatch batch;
    private BitmapFont font;
    
    private int width;
    private int height;
    
	public Lobby(Game game)
	{
		this.game = game;
		batch = new SpriteBatch();    
        font = new BitmapFont();
        font.setColor(Color.RED);
	}
	
	public void dispose() 
	{
        batch.dispose();
        font.dispose();
    }
	
	public void update(float dt)
	{
		readInput();
	}
	
	public void render()
	{
		batch.begin();
        font.draw(batch, "1. Destroyer", width * 0.5f - 100.0f, height * 0.5f - 0.0f);
        font.draw(batch, "2. Scout", width * 0.5f - 100.0f, height * 0.5f - 20.0f);
        font.draw(batch, "3. Support", width * 0.5f - 100.0f, height * 0.5f - 40.0f);
        batch.end();
	}
	
	public void resize(int width, int height) 
	{
		this.width = width;
		this.height = height;
    }
	
	public void readInput()
	{
		if (Gdx.input.isKeyPressed(Keys.NUM_1))
		{
			Gdx.app.debug("lobby", "selected destroyer");

			// Send ready event.
			Event event = new Event();
			event.createPlayerReady(this.game.getId());
			this.game.addEvent(event);
			
			this.game.nextState();
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_2))
		{
			Gdx.app.debug("lobby", "selected scout");
			
			// Send ready event.
			Event event = new Event();
			event.createPlayerReady(this.game.getId());
			this.game.addEvent(event);
			
			this.game.nextState();
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_3))
		{
			Gdx.app.debug("lobby", "selected support");
			
			// Send ready event.
			Event event = new Event();
			event.createPlayerReady(this.game.getId());
			this.game.addEvent(event);
			
			this.game.nextState();
		}
	}
}
