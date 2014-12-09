package com.mygdx.starship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class Game extends ApplicationAdapter 
{
	SpriteBatch batch;
	Texture img;
	ClientPlayer ship;
	Space space;
	
	Socket socket;
	
	private OrthographicCamera camera;
	
	ConnectionHandler connectionHandler;
	
	ProjectileManager projectileManager = null;
	
	byte id = 0;
	
	private HashMap<Byte, ClientPlayer> ships;
	
	private float ioTimer = 0.0f;
	
	private String lastKeyboardPacket = null;
	private String lastMousePacket = null;
	
	@Override
	public void create () 
	{
		//System.out.close();
		id = (byte) (int) (Math.random() * 100.0f + 1.0f);
		
		// Connect to server.
		connect();
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		ship = new ClientPlayer(id, connectionHandler);
		space = new Space();
		
		camera = new OrthographicCamera(1280, 720);
		
		
		
		ships = new HashMap<Byte, ClientPlayer>();
		
		projectileManager = new ProjectileManager();
		
		lastKeyboardPacket = new String();
		lastMousePacket = new String();
		
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(ship);
		//multiplexer.addProcessor(ship.weapon);
		Gdx.input.setInputProcessor(multiplexer);
		
		
	}

	@Override
	public void render() 
	{
		updateInput();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.set(ship.position.x, ship.position.y, 0.0f);
		camera.update();
			
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);

		space.update(dt);
		space.render(camera, ship);
		
		ship.update(dt);
		ship.render(camera);
				
		for(ClientPlayer player : ships.values())
		{
			player.update(dt);
			player.render(camera);
		}
		
		// Render projectiles.
		projectileManager.updatePhysics(dt);
		projectileManager.render(camera);

		
		// Send output.
		ioTimer += dt;
		if (ioTimer > (1.0 / 10.0f))
		{
			updateOutput();
			ioTimer = 0.0f;
		}
		
		if (Gdx.graphics.getFrameId() % 60 == 0)
		{
			System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
		}
	}
	
	public void updateInput()
	{
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			Gdx.app.exit();
		}
		
		Packet p = null;
		byte[] data = null;
		while ((p = connectionHandler.getPacket()) != null)
		{
			data = p.getData();
			
			if (data.length > 0)
			{
				String a = new String(data);
				Pattern pattern =  Pattern.compile(";");
				String[] list = pattern.split(a.subSequence(0, a.length()));
				
				// Check if position data.
				if (Byte.valueOf(list[0]) == Packet.POSITION)
				{
					int numPlayers = (list.length - 1)/4;
					//System.out.println("numplayers: " + numPlayers);
					
					for (int i = 0; i < numPlayers; i++)
					{
						
						byte pid = Byte.valueOf(list[i * 4 + 1]).byteValue();
						float x = Float.valueOf(list[i * 4 + 2]).floatValue();
						float y = Float.valueOf(list[i * 4 + 3]).floatValue();
						float dir = Float.valueOf(list[i * 4 + 4]).floatValue();
						
						// Check if data from server is intended for me.
						if (this.id == pid)
						{
							// Update position and direction.
							//System.out.println("client: update me player");
							
							ship.position.set(x, y, 0.0f);
							ship.setDirection(dir);
						}
						else
						{
							// Other player data.
							if (!ships.containsKey(pid))
							{
								// Create new player.
								//System.out.println("client: new other player data                                   !!!!!!!!!!!!!!!!!!   " + pid);
								
								
								ClientPlayer player = new ClientPlayer(pid, null);
								player.position.set(x, y, 0.0f);
								player.setDirection(dir);
								ships.put(pid, player);
								
							}
							else
							{
								// Update existing player.
								//System.out.println("client: update other player");
								
								ClientPlayer player = ships.get(pid);
								player.position.set(x, y, 0.0f);
								player.setDirection(dir);
								ships.put(pid, player);
							}
						}
					}
				}
				if (Byte.valueOf(list[0]) == Packet.PROJECTILE)
				{
					int numProjectiles = (list.length - 1)/5;
					
					/*
					System.out.println("projectiles: " + numProjectiles + " " + a);
					if (numProjectiles > 0)
					{
						System.out.println("projs : " + a);
					}
					*/
					
					//projectileManager.clear();
					
					System.out.println("client: projectile fire");
					
					for (int i = 0; i < numProjectiles; i++)
					{
						int pid = Integer.valueOf(list[i * 5 + 1]).intValue();
						float x = Float.valueOf(list[i * 5 + 2]).floatValue();
						float y = Float.valueOf(list[i * 5 + 3]).floatValue();
						float vx = Float.valueOf(list[i * 5 + 4]).floatValue();
						float vy = Float.valueOf(list[i * 5 + 5]).floatValue();
						
						projectileManager.addProjectile(pid, x, y, vx, vy);
					}
				}
			}
			else
			{
				System.err.println("client: zero size packet received");
			}
		}
	}
	
	private void connect()
	{
		SocketHints socketHints = new SocketHints();
        
		// Socket will time our in 4 seconds
        socketHints.connectTimeout = 4000;
        
        //create the socket and connect to the server entered in the text box ( x.x.x.x format ) on port 9021
        socket = Gdx.net.newClientSocket(Protocol.TCP, "127.0.0.1", 1313, socketHints);
        
        System.out.println("Connected to server " + socket.getRemoteAddress());
        
        connectionHandler = new ConnectionHandler(socket);
    	new Thread(connectionHandler).start();
	}
	
	// TODO: Must optimize this packet creater!!!
	private void updateOutput()
	{
		//if (ship.getInputArraySize() > 0)
		{
			StringBuffer a = new StringBuffer();
			a.append(id);		
			a.append(";");
			a.append(Packet.IO_KEYBOARD);
			
			byte[] ia = ship.getInputArray();
			for (int i = 0; i < ia.length; i++)
			{
				a.append(";");
				a.append(ia[i]);
			}
			a.append("\n");

			//if (!lastKeyboardPacket.equals(a.toString()))
			{
				connectionHandler.addPacket(new Packet(a.toString().getBytes()));
				//lastKeyboardPacket = a.toString();
			}
		}
	
		
		//if (ship.weapon.getInputArraySize() > 0)
		{
			/*
			Vector2 pos = ship.weapon.getWorldPosition();
			pos.add(ship.position.x, ship.position.y);
			
			StringBuffer a = new StringBuffer();
			a.append(id);		
			a.append(";");
			a.append(Packet.IO_MOUSE);
			
			a.append(";");
			a.append(pos.x);
			a.append(";");
			a.append(pos.y);
			a.append(";");
			a.append(ship.weapon.getMouseButton());
			a.append("\n");
			
			//System.out.println("mouse " + a.toString());

			if (!lastMousePacket.equals(a.toString()))
			{
				connectionHandler.addPacket(new Packet(a.toString().getBytes()));
				//lastMousePacket = a.toString();
			}
			*/
			
		}
	}
}
