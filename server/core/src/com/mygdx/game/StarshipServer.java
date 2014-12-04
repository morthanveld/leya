package com.mygdx.game;

import java.io.IOException;
import java.io.PrintStream;
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
		//System.out.close();
		
		players = new Array<Player>();
		
		// Start listening on incoming clients.
		listen();
	}
	
	public void render()
	{
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60f);
		
		updateInput();
		
		update(dt);
		
		try 
		{
			Thread.sleep((long)(1000/60-Gdx.graphics.getDeltaTime()));
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Game Update
	 * Physics and AI.
	 */
	public void update(float dt)
	{
		StringBuffer a = new StringBuffer();
		a.append(Packet.POSITION);
		
		for (Player p : players)
		{
			// Update player ship.
			p.update(dt);
			
			// Compile data to send.
			a.append(";");
			a.append(p.getId());
			a.append(";");
			a.append(p.getPosition().x);
			a.append(";");
			a.append(p.getPosition().y);
			a.append(";");
			a.append(p.getDirection());
		}
		
		a.append("\n");
		Packet packet = new Packet(a.toString().getBytes());

		// Add position packet to players connection.
		for (Player p : players)
		{
			p.addPacket(packet);
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
