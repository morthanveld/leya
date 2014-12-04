package com.mygdx.game;

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
	
	public Player(ConnectionHandler connection)
	{
		this.id = 0;
		this.name = new String(connection.toString());
		this.connection = connection;
		
		position = new Vector2();
		velocity = new Vector2();
		acceleration = new Vector2();
		
		orientation = new Quaternion();
		direction = 0.0f;
		angularVelocity = 0.0f;
		angularAcceleration = 0.0f;
			
		drivePower = 3200.0f * 5.0f;
		turnPower = 3000.0f * 0.1f;
		
		
	}
	
	public void update(float dt)
	{
		// Receive network data.
		receivePacket();
		
		// Update ship physics.

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
		
		System.out.println(position);
		
		/*
		if (id != 0)
		{
			StringBuffer a = new StringBuffer();
			a.append(id);
			a.append(";");
			a.append(Packet.POSITION);
			a.append(";");
			a.append(position.x);
			a.append(";");
			a.append(position.y);
			a.append(";");
			a.append(direction);
			a.append("\n");
			byte[] data = a.toString().getBytes();

			connection.addPacket(new Packet(data));
		}
		*/
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
				
				for (byte j : data)
				{
					System.out.println(j + " ");
				}
				System.out.println();
				
				
				System.out.println("server: data from client id " + data[0]);
				
				if (id == 0)
				{
					id = data[0];
				}
				
				if (data[1] == 'A')
				{
					// Keyboard input from player.
					System.out.println("server: keyboard input from player");
					
					for (int k = 2; k < data.length; k++)
					{
						handleInput(data[k]);
					}
				}
				else if (data[1] == 'B')
				{
					// Mouse input from player.
					System.out.println("server: mouse input from player");
				}
			}
			else
			{
				System.err.println("server: zero size packet received");
			}
		}
	}
	
	private void handleInput(byte key)
	{
		if (key == Keys.A)
		{
			angularAcceleration += turnPower;
		}
		if (key == Keys.D)
		{
			angularAcceleration += -turnPower;
		}
		if (key == Keys.W)
		{
			acceleration.set(0.0f, drivePower);
			acceleration.rotate(direction);
		}
		if (key == Keys.S)
		{
			acceleration.set(0.0f, -drivePower);
			acceleration.rotate(direction);
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
}
