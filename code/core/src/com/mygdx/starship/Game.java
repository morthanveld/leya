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
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Event;
import com.mygdx.game.Utils;

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
	
	private Array<Event> events = null;
	
	byte id = 0;
	
	private HashMap<Byte, ClientShip> ships;
	
	private float ioTimer = 0.0f;
	
	private ClientInput clientInput = null;
	
	private float inboxSum = 0.0f;
	private float outboxSum = 0.0f;
	
	private Pattern regexPattern = null;
	
	private Lobby lobby = null;
	
	private int state = 0;
	
	static final int STATE_LOBBY = 0x1;
	static final int STATE_WAITING_FOR_PLAYERS = 0x2;
	static final int STATE_LOADING_LEVEL = 0x3;
	static final int STATE_GAME_RUNNING = 0x4;
	
	@Override
	public void create () 
	{
		//System.out.close();
		id = (byte) (int) (Math.random() * 100.0f + 1.0f);
		
		events = new Array<Event>();
		
		regexPattern = Pattern.compile(";");
		
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
		
		ships.put(id, ship);
		//Gdx.input.setInputProcessor(clientInput);

		lobby = new Lobby(this);
		
		this.state = STATE_LOBBY;
		
		Gdx.app.setLogLevel(3);
	}

	@Override
	public void render() 
	{
		long start = System.currentTimeMillis();
		inboxSum += connectionHandler.getInboxSize();
		updateInput();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.position.set(ship.getPosition(), 0.0f);
		camera.update();
			
		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);
		//dt = 1.0f / 120.0f;

		if (this.state == STATE_LOBBY)
		{
			this.lobby.update(dt);
			this.lobby.render();
		}
		else if (this.state == STATE_WAITING_FOR_PLAYERS)
		{
			// Set input to ship control.
			clientInput = new ClientInput(ship);
			Gdx.input.setInputProcessor(clientInput);
			nextState();
		}
		else if (this.state == STATE_LOADING_LEVEL)
		{
			// Wait for level to load.
			nextState();
		}
		else if (this.state == STATE_GAME_RUNNING)
		{
			space.update(dt);
			space.render(camera, ship);

			for(ClientShip player : ships.values())
			{
				player.update(dt);
				player.render(camera);
			}

			// Render projectiles.
			projectileManager.updatePhysics(dt);
			projectileManager.render(camera);
		}

		outboxSum += connectionHandler.getOutboxSize();
		
		// Send output.
		ioTimer += dt;
		if (ioTimer > (1.0f / 60.0f))
		{
			updateOutput();
			ioTimer = 0.0f;
		}
		
		if (Gdx.graphics.getFrameId() % 60 == 0)
		{
			System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond() + 
					"\tINBOX: " + inboxSum/60.0f + 
					"\tOUTBOX: " + outboxSum/60.0f +
					"\tCPU: " + (System.currentTimeMillis() - start)
					);
			inboxSum = 0;
			outboxSum = 0;
		}
	}
	
	public void resize(int width, int height) 
	{
		if (this.lobby != null)
		{
			this.lobby.resize(width, height);
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
				//Pattern pattern =  Pattern.compile(";");
				//String[] list = regexPattern.split(a.subSequence(0, a.length()));
				String[] list = regexPattern.split(a);
				
				//System.out.println(a);
				
				// Check if position data.
				if (Byte.valueOf(list[0]) == Packet.POSITION)
				{
					int numPlayers = (list.length - 1)/5;
					//System.out.println("numplayers: " + numPlayers);
					//System.err.println("position data " + a);
					
					for (int i = 0; i < numPlayers; i++)
					{
						
						byte pid = Byte.valueOf(list[i * 5 + 1]).byteValue();
						byte type = Byte.valueOf(list[i * 5 + 2]).byteValue();
						float x = Utils.upScale(Float.valueOf(list[i * 5 + 3]).floatValue());
						float y = Utils.upScale(Float.valueOf(list[i * 5 + 4]).floatValue());
						float dir = Float.valueOf(list[i * 5 + 5]).floatValue();
											
						// Other player data.
						if (!ships.containsKey(pid))
						{
							// Create new player.
							//								System.out.println("client: new other player data                                   !!!!!!!!!!!!!!!!!!   " + pid + "\t" + this.id);


							ClientShip player = new ClientShip(pid, null);
							player.setPosition(x, y);
							player.setDirection(dir);
							player.setType(type);
							ships.put(pid, player);

						}
						else
						{
							// Update existing player.
							//System.out.println("client: update other player " + pid + "\t" + x + "\t" + y);

							ClientShip player = ships.get(pid);
							player.setPosition(x, y);
							player.setDirection(dir);
							//player.setType(type);
							ships.put(pid, player);
						}
					}
				}
				if (Byte.valueOf(list[0]) == Packet.PROJECTILE)
				{
					int numProjectiles = (list.length - 1)/3;
					projectileManager.clear();
					
					for (int i = 0; i < numProjectiles; i++)
					{
						int pid = Integer.valueOf(list[i * 3 + 1]).intValue();
						float x = Utils.upScale(Float.valueOf(list[i * 3 + 2]).floatValue());
						float y = Utils.upScale(Float.valueOf(list[i * 3 + 3]).floatValue());
						/*float vx = Utils.upScale(Float.valueOf(list[i * 5 + 4]).floatValue());
						float vy = Utils.upScale(Float.valueOf(list[i * 5 + 5]).floatValue());
						*/
						
						projectileManager.addProjectile(pid, x, y, x, y);
					}
				}
				
				if (Byte.valueOf(list[0]) == Packet.EVENT)
				{
					//System.out.println("event packet " + a);
					int events = (list.length - 1)/2;
					
					for (int i = 0; i < events; i++)
					{
						int type = Integer.valueOf(list[i * 2 + 1]).intValue();
						byte pid = Byte.valueOf(list[i * 2 + 2]).byteValue();
						
						if (type ==  Event.EVENT_ENTITY_DESTROY)
						{
							System.out.println("destroy " + pid);
							destroyEntity(pid);
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
	
	private void destroyEntity(byte id)
	{
		ships.remove(id);
	}
	
	private void connect()
	{
		SocketHints socketHints = new SocketHints();
        
		// Socket will time our in 4 seconds
        socketHints.connectTimeout = 4000;
        
        //create the socket and connect to the server entered in the text box ( x.x.x.x format ) on port 9021
        socket = Gdx.net.newClientSocket(Protocol.TCP, "127.0.0.1", 1315, socketHints);
        
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
		
		if (events.size > 0)
		{
			StringBuffer eventBuffer = new StringBuffer();
			eventBuffer.append(id);		
			eventBuffer.append(";");
			eventBuffer.append(Packet.EVENT);
			
			while (events.size > 0)
			{
				Event event = events.pop();
				
				switch (event.getType())
				{
				case Event.EVENT_ENTITY_DESTROY:
				{						
					eventBuffer.append(";");
					eventBuffer.append(event.getType());
					eventBuffer.append(";");
					eventBuffer.append(event.getEntityId());
					break;
				}
				case Event.EVENT_PLAYER_READY:
				{
					eventBuffer.append(";");
					eventBuffer.append(event.getType());
					eventBuffer.append(";");
					eventBuffer.append(event.getEntityId());
					break;					
				}
				}
			}
			
			eventBuffer.append("\n");
			
			connectionHandler.addPacket(new Packet(eventBuffer.toString().getBytes()));
		}
	}
	
	public void addEvent(Event e)
	{
		events.add(e);
	}
	
	/**
	 * Method to change game state.
	 */
	public void nextState()
	{
		switch (this.state)
		{
		case STATE_LOBBY:
		{
			this.state = STATE_LOADING_LEVEL;
			break;
		}
		case STATE_LOADING_LEVEL:
		{
			this.state = STATE_WAITING_FOR_PLAYERS;
			break;
		}
		case STATE_WAITING_FOR_PLAYERS:
		{
			this.state = STATE_GAME_RUNNING;
			break;
		}
		case STATE_GAME_RUNNING:
		{
			break;
		}
		}
	}
	
	public byte getId()
	{
		return this.id;
	}
}
