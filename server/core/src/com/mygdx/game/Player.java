package com.mygdx.game;

import java.util.regex.Pattern;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player 
{
	private ConnectionHandler connection;
	
	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;
	
	private Quaternion orientation;
	private float direction;
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
	
	private Body body = null;
	
	public Player(StarshipServer server, ConnectionHandler connection)
	{
		this.id = 0;
		this.connection = connection;
		this.server = server;
		
		position = new Vector2();
		velocity = new Vector2();
		acceleration = new Vector2();
		
		orientation = new Quaternion();
		direction = 0.0f;
		angularAcceleration = 0.0f;
			
		drivePower = 3200.0f * 0.1f * 1000.0f;
		turnPower = 3000.0f * 0.03f * 10000.0f;
		
		weapon = new Weapon(this);
		
		setupPhysics();
	}
	
	public void setupPhysics()
	{
		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(this.position);
		bodyDef.angularDamping = 0.2f;
		bodyDef.linearDamping = 0.2f;

		// Create our body in the world using our body definition
		this.body = this.server.getWorld().createBody(bodyDef);

		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(32.0f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit

		// Create our fixture and attach it to the body
		//Fixture fixture = 
		body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
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
		
		// Apply angular movement to player.
		body.applyTorque(angularAcceleration, true);

		// Apply linear movement to player.
		Vector2 a = new Vector2(acceleration);
		a.rotate(body.getAngle() * 180.0f / 3.141592f);
		body.applyForceToCenter(a, true);

 
		position.set(body.getPosition());
		orientation.set(new Vector3(0.0f, 0.0f, 1.0f), body.getAngle());
		this.direction = body.getAngle() * 180.0f / 3.141592f;
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
					
					acceleration.set(0.0f, 0.0f);
					angularAcceleration = 0.0f;
					
					if (Byte.valueOf(list[KEY_A + 2]).byteValue() > 0)
					{
						angularAcceleration = turnPower;
					}
					if (Byte.valueOf(list[KEY_D + 2]).byteValue() > 0)
					{
						angularAcceleration = -turnPower;
					}
					if (Byte.valueOf(list[KEY_W + 2]).byteValue() > 0)
					{
						acceleration.set(0.0f, drivePower);
						//acceleration.rotate(direction);
					}
					if (Byte.valueOf(list[KEY_S + 2]).byteValue() > 0)
					{
						acceleration.set(0.0f, -drivePower);
						//acceleration.rotate(direction);
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
					float x = Float.valueOf(list[2]).floatValue();
					float y = Float.valueOf(list[3]).floatValue();
					int button = Integer.valueOf(list[4]).intValue();
					
					weapon.setTarget(new Vector2(x, y));
					if (button >= 0)
					{
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
		return server.getProjectileManager();
	}
}
