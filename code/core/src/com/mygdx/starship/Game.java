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
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class Game extends ApplicationAdapter 
{
	SpriteBatch batch;
	Texture img;
	Ship ship;
	Space space;
	
	Socket socket;
	
	private OrthographicCamera camera;
	
	ConnectionHandler connectionHandler;
	
	byte id = 0;
	
	private HashMap<Byte, Ship> ships;
	
	@Override
	public void create () 
	{
		System.out.close();
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		ship = new Ship();
		space = new Space();
		
		camera = new OrthographicCamera(1280, 720);
		
		id = (byte) (Math.random() * 100 + 1);
		
		ships = new HashMap<Byte, Ship>();
		
		// Connect to server.
		connect();
	}

	@Override
	public void render () 
	{
		updateInput();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.set(ship.position.x, ship.position.y, 0.0f);
		camera.update();
			
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60f);

		space.update(dt);
		space.render(camera, ship);
		
		ship.update(dt);
		ship.render(camera);
		
		for(Ship player : ships.values())
		{
			player.update(dt);
			player.render(camera);
		}
		
		receivePacket();
		sendPlayerInput();
	}
	
	public void receivePacket()
	{
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
					System.out.println("numplayers: " + numPlayers);
					
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
							System.out.println("client: update me player");
							
							ship.position.set(x, y, 0.0f);
							ship.setDirection(dir);
						}
						else
						{
							// Other player data.
							if (!ships.containsKey(pid))
							{
								// Create new player.
								System.out.println("client: new other player data                                   !!!!!!!!!!!!!!!!!!   " + pid);
								
								Ship player = new Ship();
								player.position.set(x, y, 0.0f);
								player.setDirection(dir);
								ships.put(pid, player);
							}
							else
							{
								// Update existing player.
								System.out.println("client: update other player");
								
								Ship player = ships.get(pid);
								player.position.set(x, y, 0.0f);
								player.setDirection(dir);
								ships.put(pid, player);
							}
						}
					}
				}
			}
			else
			{
				System.err.println("client: zero size packet received");
			}
		}
	}
	
	public void updateInput()
	{
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			Gdx.app.exit();
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
	private void sendPlayerInput()
	{
		if (ship.getNewInput())
		{
			byte[] a = ship.getInputArray();
			byte[] b = new byte[a.length + 2];
			b[0] = id;
			b[1] = 'A';
			System.arraycopy(a, 0, b, 2, a.length);

			connectionHandler.addPacket(new Packet(b));
		}
	
		
		if (ship.weapon.getNewInput())
		{
			byte[] a = ship.weapon.getInputArray();
			byte[] b = new byte[a.length + 2];
			b[0] = id;
			b[1] = 'B';
			System.arraycopy(a, 0, b, 2, a.length);

			connectionHandler.addPacket(new Packet(b));
		}
	}
	
	private float byteToFloat(byte a, byte b, byte c, byte d)
	{
		/*
		int asInt = (a & 0xFF) 
	            | ((b & 0xFF) << 8) 
	            | ((c & 0xFF) << 16) 
	            | ((d & 0xFF) << 24);
		
		return Float.intBitsToFloat(asInt);
		*/
		byte[] bytes = {a, b, c, d};
		float f = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
		return f;
	}
	
	
}
