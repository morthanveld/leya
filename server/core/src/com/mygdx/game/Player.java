package com.mygdx.game;

import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Player 
{
	private String name;
	private ConnectionHandler connection;
	
	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;
	
	private Quaternion orientation;
	private float direction;
	private float angularVelocity;
	private float angularAcceleration;
		
	private float drivePower;
	private float turnPower;
	
	private byte id;
	
	private StarshipServer server = null;
	
	private Weapon weapon = null; 
	
	private static final int KEY_A = 0;
	private static final int KEY_W = 1;
	private static final int KEY_S = 2;
	private static final int KEY_D = 3;
	private static final int KEY_Q = 4;
	private static final int KEY_E = 5;
	
	public Player(StarshipServer server, ConnectionHandler connection)
	{
		this.id = 0;
		this.name = new String(connection.toString());
		this.connection = connection;
		this.server = server;
		
		position = new Vector2();
		velocity = new Vector2();
		acceleration = new Vector2();
		
		orientation = new Quaternion();
		direction = 0.0f;
		angularVelocity = 0.0f;
		angularAcceleration = 0.0f;
			
		drivePower = 3200.0f * 10.0f;
		turnPower = 3000.0f * 0.3f;
		
		weapon = new Weapon(this);
	}
	
	public void updatePhysics(float dt)
	{
		if (connection.isDisconnected())
		{
			// If connection dies, unregister player at server.
			server.unregisterPlayer(this);
		}
		
		// Receive network data.
		receivePacket();
		
		// Update ship physics.
		weapon.update(dt);

		// Reduce acceleration to zero gradually.
		acceleration.set(acceleration.x * 0.98f * dt, acceleration.y * 0.98f * dt);

		velocity.mulAdd(acceleration, dt);

		// Put some viscosity in space. Velocity in opposite direction.
		velocity.mulAdd(velocity, -0.25f * dt);

		position.mulAdd(velocity, dt);
		orientation.setFromAxis(0.0f, 0.0f, 1.0f, direction);

		angularAcceleration *= 0.98f;
		angularVelocity = angularAcceleration * dt;
		direction += angularVelocity * dt;
	
		//System.out.println(id + " | " + connection.getInboxSize() + " | " + connection.getOutboxSize());
	}
	
	public void addPacket(Packet p)
	{
		connection.addPacket(p);
	}
	
	public void receivePacket()
	{
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
								
				if (id == 0)
				{
					id = pid;
				}
				
				if (type == Packet.IO_KEYBOARD)
				{
					// Keyboard input from player.
					//System.out.println("server: keyboard input from player");
					
					if (Byte.valueOf(list[KEY_A + 2]).byteValue() > 0)
					{
						angularAcceleration += turnPower;
					}
					
					if (Byte.valueOf(list[KEY_D + 2]).byteValue() > 0)
					{
						angularAcceleration -= turnPower;
					}
					
					if (Byte.valueOf(list[KEY_W + 2]).byteValue() > 0)
					{
						acceleration.set(0.0f, drivePower);
						acceleration.rotate(direction);
					}
					
					if (Byte.valueOf(list[KEY_S + 2]).byteValue() > 0)
					{
						acceleration.set(0.0f, -drivePower);
						acceleration.rotate(direction);
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
					//System.out.println("server: mouse input from player");
					//System.out.println("server: " + new String(data));
					
					float x = Float.valueOf(list[2]).floatValue();
					float y = Float.valueOf(list[3]).floatValue();
					int button = Integer.valueOf(list[4]).intValue();
					
					weapon.setTarget(new Vector2(x, y));
					if (button >= 0)
					{
						//System.out.println("server: fire " + button);
						weapon.fire();
					}
				}
			}
			else
			{
				System.err.println("server: zero size packet received");
			}
		}
	}
	
	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public float getDirection() {
		return direction;
	}

	public byte getId() {
		return id;
	}
	
	public ProjectileManager getProjectileManager()
	{
		return server.projectileManager;
	}
}
