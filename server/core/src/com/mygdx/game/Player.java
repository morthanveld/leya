package com.mygdx.game;

import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player extends Ship
{
	private ConnectionHandler connection = null;
	private StarshipServer server = null;
	
	private Weapon weapon = null; 
	
	private static final int KEY_A = 0;
	private static final int KEY_W = 1;
	private static final int KEY_S = 2;
	private static final int KEY_D = 3;
	private static final int KEY_Q = 4;
	private static final int KEY_E = 5;
	
	private boolean isReady = false;
	
	public Player(StarshipServer server, ConnectionHandler connection)
	{
		super(Entity.ENTITY_PLAYER);
		this.connection = connection;
		this.server = server;
		
		super.setBoundingRadius(Utils.downScale(32.0f));
		super.setMaxLinearSpeed(1000.0f);
		super.setMaxLinearAcceleration(1000.0f);
		super.setMaxAngularSpeed(1000.0f);
		super.setMaxAngularAcceleration(1000.0f);
		
		// Setup thrusters.
		super.setAxialThrustPower(0.03f);
		super.setLongitudinalThrustPower(0.1f);
		super.setLateralThrustPower(0.1f);
		
		weapon = new Weapon(this);
	}
	
	public void createBody(Vector2 position)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.angularDamping = 0.5f;
		bodyDef.linearDamping = 0.2f;

		CircleShape circle = new CircleShape();
		circle.setRadius(super.getBoundingRadius());

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		fixtureDef.filter.categoryBits = StarshipServer.CATEGORY_PLAYER;
		fixtureDef.filter.maskBits = StarshipServer.MASK_PLAYER;
		fixtureDef.filter.groupIndex = 0;
		
		super.createBody(bodyDef, fixtureDef);

		circle.dispose();
	}
	
	public void update(float dt)
	{
		if (connection != null)
		{
			if (connection.isDisconnected())
			{
				// If connection dies, unregister player at server.
				server.unregisterPlayer(this);
			}
		}

		// Receive network data.
		receivePacket();

		// Update ship physics.
		weapon.update(dt);
	}
	
	public float calculateOrientationFromLinearVelocity(Vector2 from, Vector2 to)
	{
		return from.angle(to);
	}
	
	public void addPacket(Packet p)
	{
		if (connection != null)
		{
			connection.addPacket(p);
		}
	}
	
	public void receivePacket()
	{
		if (connection == null)
		{
			return;
		}
		
		Packet p = null;
		byte[] data = null;
		while ((p = connection.getPacket()) != null)
		{
			data = p.getData();
			
			if (data.length > 0)
			{
				String a = new String(data);
				//System.out.println(a);
				Pattern pattern =  Pattern.compile(";");
				String[] list = pattern.split(a.subSequence(0, a.length()));
				
				byte pid = Byte.valueOf(list[0]).byteValue();
				byte type = Byte.valueOf(list[1]).byteValue();
								
				if (super.getId() == 0)
				{
					super.setId(pid);
				}
				
				if (type == Packet.IO_KEYBOARD)
				{
					super.setLongitudinalThrust(0.0f);
					super.setAxialThrust(0.0f);
					
					if (Byte.valueOf(list[KEY_A + 2]).byteValue() > 0)
					{
						super.setAxialThrust(super.getAxialThrustPower());
					}
					if (Byte.valueOf(list[KEY_D + 2]).byteValue() > 0)
					{
						super.setAxialThrust(-super.getAxialThrustPower());
					}
					if (Byte.valueOf(list[KEY_W + 2]).byteValue() > 0)
					{
						super.setLongitudinalThrust(super.getLongitudinalThrustPower());
					}
					if (Byte.valueOf(list[KEY_S + 2]).byteValue() > 0)
					{
						super.setLongitudinalThrust(-super.getLongitudinalThrustPower());
					}
					if (Byte.valueOf(list[KEY_Q + 2]).byteValue() > 0)
					{
					}
					if (Byte.valueOf(list[KEY_E + 2]).byteValue() > 0)
					{
					}
				}
				else if (type == Packet.IO_MOUSE)
				{
					// Mouse input from player.
					float x = Utils.downScale(Float.valueOf(list[2]).floatValue());
					float y = Utils.downScale(Float.valueOf(list[3]).floatValue());
					int button = Integer.valueOf(list[4]).intValue();
					
					weapon.setTarget(new Vector2(x, y));
					if (button >= 0)
					{
						weapon.fire();
					}
				}
				else if (type == Packet.EVENT)
				{
					//System.out.println("event packet " + a);
					int events = (list.length - 2)/2;
					
					for (int i = 0; i < events; i++)
					{
						int eventType = Integer.valueOf(list[i * 2 + 2]).intValue();
						byte eventId = Byte.valueOf(list[i * 2 + 3]).byteValue();
						
						if (eventType ==  Event.EVENT_ENTITY_DESTROY)
						{
							Gdx.app.debug("Player", "entity destroy - do nothing");
						}
						else if (eventType == Event.EVENT_PLAYER_READY)
						{
							Gdx.app.debug("Player", "player ready");
							
							// Tell game that player is ready.
							this.isReady = true;
						}
					}
				}
			}
			else
			{
				System.err.println("server: zero size packet received");
			}
		}
	}

	public boolean isReady()
	{
		return this.isReady;
	}
	
	public ProjectileManager getProjectileManager()
	{
		return server.getGame().getProjectileManager();
	}
	
}
