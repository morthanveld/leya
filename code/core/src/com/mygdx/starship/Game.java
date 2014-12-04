package com.mygdx.starship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	@Override
	public void create () 
	{
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		ship = new Ship();
		space = new Space();
		
		camera = new OrthographicCamera(1280, 720);
		
		id = (byte) (Math.random() * 200 + 1);
		
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
		
		/*
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		*/
			
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60f);

		space.update(dt);
		space.render(camera, ship);
		
		ship.update(dt);
		ship.render(camera);
			
		// Send player input.
//		connectionHandler.addPacket(new Packet(ship.getInputArray()));
		//networkWrite();
		
		receivePacket();
		
		sendPlayerInput();
		//connectionHandler.addPacket(new Packet("ABC\n".getBytes()));
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
				for (byte j : data)
				{
					System.out.print(j + " ");
				}
				System.out.println();
				
			
				
				String a = new String(data);
				
				/*
				System.out.println("a: " + a);
				int startIdx = a.indexOf(";");
				String b = a.substring(startIdx + 1);
				System.out.println("b: " + b);
				int endIdx = b.indexOf(";");
				String x = b.substring(0, endIdx);

				String tmp = b.substring(endIdx + 1);
				endIdx = tmp.indexOf(";");
				*/
				Pattern pattern =  Pattern.compile(";");
				String[] list = pattern.split(a.subSequence(0, a.length()));
				
				
				for (int i = 0; i < list.length; i++)
				{
					System.out.println(list[i]);
				}
				
				
				/*
				System.out.println("x: '" + x + "'    " + Float.valueOf(x).floatValue());
				System.out.println("y: '" + y + "'    " + Float.valueOf(y).floatValue());
				*/
				
				

				// Check if position data.
				if (Byte.valueOf(list[0]) == Packet.POSITION)
				{
					int numPlayers = (list.length - 1)/4;
					System.out.println("numplayers: " + numPlayers);
					
					for (int i = 0; i < numPlayers; i++)
					{
						// Check if data from server is intended for me.
						if (Byte.valueOf(list[i * 1 + 1]) == this.id)
						{
							// Update position and direction.
							ship.position.set(Float.valueOf(list[i * 1 + 2]).floatValue(), Float.valueOf(list[i * 1 + 3]).floatValue(), 0.0f);
							ship.setDirection(Float.valueOf(list[i * 1 + 4]).floatValue());
						}
					}
				}
				
				
				/*
				if (data[0] == Packet.POSITION)
				{
					// Keyboard input from player.
					System.out.println("client: position from server");
					
					
					for (int k = 1; k < data.length; k++)
					{
						//handleInput(data[k]);
						System.out.print(data[k] + " ");
					}
					System.out.println();
					
					
					System.out.println(new String(data));

					
					float x = byteToFloat(data[1], data[2], data[3], data[4]);
					float y = byteToFloat(data[5], data[6], data[7], data[8]);
					
					System.out.println(x + "\t" + y);
					ship.position.set(x, y, 0.0f);
					
				}
			*/
			
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
