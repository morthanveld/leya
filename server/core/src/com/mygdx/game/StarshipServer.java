package com.mygdx.game;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;

public class StarshipServer extends ApplicationAdapter 
{
	Array<Player> players;
	
	public void create () 
	{
		players = new Array<Player>();
		
		// Start listening on incoming clients.
		listen();
	}
	
	public void render()
	{
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60f);
		
		updateInput();
		
		update(dt);
	}
	
	/*
	 * Game Update
	 * Physics and AI.
	 */
	public void update(float dt)
	{
		for (Player p : players)
		{
			p.update(dt);
		}
	}
	
	public void updateInput()
	{
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			Gdx.app.exit();
		}
	}
	
	public void listen()
	{
		Runnable PlayerLobby = new PlayerLobby(this);
		new Thread(PlayerLobby).start();
	}
	
	public void registerPlayer(ConnectionHandler connection)
	{
		players.add(new Player(connection));
	}
}
