package com.mygdx.starship;

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
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class Game extends ApplicationAdapter 
{
	SpriteBatch batch;
	Texture img;
	ClientShip ship;
	Space space;
	
	Socket socket;
	
	private OrthographicCamera camera;
	
	ConnectionHandler connectionHandler;
	
	ProjectileManager projectileManager = null;
	
	byte id = 0;
	
	private HashMap<Byte, ClientShip> ships;
	
	private float ioTimer = 0.0f;
	
	private ClientInput clientInput = null;
	
	@Override
	public void create () 
	{
		//System.out.close();
		id = (byte) (int) (Math.random() * 100.0f + 1.0f);
		
		// Connect to server.
		connect();
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		ship = new ClientShip(id, connectionHandler);
		space = new Space();
		
		camera = new OrthographicCamera(1280, 720);
		
		
		
		ships = new HashMap<Byte, ClientShip>();
		
		projectileManager = new ProjectileManager();
			
		clientInput = new ClientInput(ship);
		Gdx.input.setInputProcessor(clientInput);
	}

	@Override
	public void render() 
	{
		updateInput();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.position.set(ship.getPosition(), 0.0f);
		camera.update();
			
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);

		space.update(dt);
		space.render(camera, ship);
		
		ship.update(dt);
		ship.render(camera);
				
		for(ClientShip player : ships.values())
		{
			player.update(dt);
			player.render(camera);
		}
		
		// Render projectiles.
		
		projectileManager.updatePhysics(dt);
		projectileManager.render(camera);

		
		// Send output.
		ioTimer += dt;
		if (ioTimer > (1.0 / 60.0f))
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
		
		// Update client input.
		clientInput.updateInput();
		
		// Read input from connection.
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
					//System.err.println("position data");
					
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
							
							ship.setPosition(x, y);
							ship.setDirection(dir);
						}
						else
						{
							// Other player data.
							if (!ships.containsKey(pid))
							{
								// Create new player.
								System.out.println("client: new other player data                                   !!!!!!!!!!!!!!!!!!   " + pid + "\t" + this.id);
								
								
								ClientShip player = new ClientShip(pid, null);
								player.setPosition(x, y);
								player.setDirection(dir);
								ships.put(pid, player);
								
							}
							else
							{
								// Update existing player.
								//System.out.println("client: update other player");
								
								ClientShip player = ships.get(pid);
								player.setPosition(x, y);
								player.setDirection(dir);
								ships.put(pid, player);
							}
						}
					}
				}
				if (Byte.valueOf(list[0]) == Packet.PROJECTILE)
				{
					int numProjectiles = (list.length - 1)/5;
					
					//System.err.println(a);
					
					//System.out.println("projectiles: " + numProjectiles + " " + a);
					/*
					if (numProjectiles > 0)
					{
						System.out.println("projs : " + a);
					}
					*/
					
					projectileManager.clear();
					
					//System.out.println("client: projectile fire");
					//System.err.println("projectile data");
					
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
        socket = Gdx.net.newClientSocket(Protocol.TCP, "127.0.0.1", 1316, socketHints);
        
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
			
			byte[] ia = clientInput.getInputArray();
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
