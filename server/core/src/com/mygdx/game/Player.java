package com.mygdx.game;

import java.util.regex.Pattern;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player implements Steerable<Vector2>
{
	private ConnectionHandler connection;
	
	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;
	
	private Quaternion orientation;
	private float direction;
	private float angularAcceleration;
	private float newDirection;
		
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
	
	private float radius = Utils.downScale(32.0f);
	private boolean tagged = false; 
	
	private SteeringBehavior<Vector2> steeringBehavior = null;
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	private float maxLinearSpeed = 0.1f; //320000.0f;
	private float maxLinearAcceleration = 0.3f; //320000.0f;
	private float maxAngularSpeed = 0.1f; //100000.0f;
	private float maxAngularAcceleration = 0.01f; //3000000.0f;
	
	private float life = 100.0f;
	
	private byte type;
	
	private static final byte ACTOR_PLAYER = 0;
	private static final byte ACTOR_AGENT = 1;
	
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
		newDirection = 0.0f;
			
		drivePower = 0.1f; //3200.0f * 0.1f * 1000.0f;
		turnPower = 0.03f; //3000.0f * 0.1f * 10000.0f;
		
		weapon = new Weapon(this);
					
		if (connection != null)
		{
			// TODO: This is a special case for playable clients.
			setupPhysics();
			this.type = ACTOR_PLAYER;
		}
		else
		{
			// Agent
			this.id = (byte) (int) (Math.random() * 100.0f + 1.0f);
			this.type = ACTOR_AGENT;
		}
		
		//steeringBehavior = new Seek<Vector2>(this);
	}
	
	public void setupPhysics()
	{
		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(this.position);
		bodyDef.angularDamping = 0.5f;
		bodyDef.linearDamping = 0.2f;

		// Create our body in the world using our body definition
		this.body = this.server.getWorld().createBody(bodyDef);

		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(this.radius);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit
		
		if (this.connection != null)
		{
			fixtureDef.filter.categoryBits = StarshipServer.CATEGORY_PLAYER;
			fixtureDef.filter.maskBits = StarshipServer.MASK_PLAYER;
			fixtureDef.filter.groupIndex = 0;
		}
		else
		{
			fixtureDef.filter.categoryBits = StarshipServer.CATEGORY_ENEMY;
			fixtureDef.filter.maskBits = StarshipServer.MASK_ENEMY;
			fixtureDef.filter.groupIndex = 0;
		}

		// Create our fixture and attach it to the body
		//Fixture fixture = 
		body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
	}
	
	public void setSteeringBehavior(SteeringBehavior sb)
	{
		this.steeringBehavior = sb;
	}
	
	public void updatePhysics(float dt)
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
	
	public void updateAi(float dt)
	{
		 if (steeringBehavior != null && this.connection == null) 
		 {
			 // Calculate steering acceleration
			 steeringBehavior.calculateSteering(steeringOutput);
			 
			 Vector2 ll = new Vector2(steeringOutput.linear);
			 Vector2 dd = new Vector2(0.0f, 1.0f);
			 dd.rotate(this.direction);
			 float vv = ll.dot(dd);
			 acceleration.set(0.0f, vv);
			 
			 //System.out.println("linear: " + ll + "\t" + acceleration);
			 //System.out.println(steeringOutput.linear);
			 
			 Vector2 linear = new Vector2(steeringOutput.linear);
			 Vector2 from = new Vector2(0.0f, 1.0f);
			 newDirection = from.angle(linear);

			 Vector2 dv = new Vector2(0.0f, 1.0f);
			 dv.rotate(body.getAngle() * 180.0f / 3.141592f);
			 
			 float diff = linear.angle(dv);
			 
			 float as = this.body.getAngularVelocity();
			 float damp = (Math.abs(diff)/360.0f);
			 this.body.setAngularDamping(Math.max(1.0f - damp * damp * damp, 0.5f));
			 
			 angularAcceleration = -diff / 45.0f * this.maxAngularAcceleration;
		 }
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

	@Override
	public float getMaxLinearSpeed() {
		return this.maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return this.maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return this.maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return this.maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return this.radius;
	}

	@Override
	public boolean isTagged() {
		return this.tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}

	@Override
	public Vector2 newVector() {
		return new Vector2();
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return (float)Math.atan2(-vector.x, vector.y);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
	}

	public byte getType() {
		return type;
	}
}
