package com.mygdx.starship;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Ship
{
	
	public Vector3 position;
	public Vector3 velocity;
	private Vector3 acceleration;
	
	private Quaternion orientation;
	private float direction;
	private float angularVelocity;
	private float angularAcceleration;
		
	private Vector3 scale;
	
	
	private float drivePower;
	private float turnPower;
	
	
	SpriteBatch batch;
	Texture img;
	
	public Weapon weapon;
	
	private byte[] inputArray = null;
	private boolean newInput = false;
	
	public Ship()
	{
		batch = new SpriteBatch();
		img = new Texture("fighter.png");
		
		weapon = new Weapon(this);
		
		position = new Vector3();
		velocity = new Vector3();
		acceleration= new Vector3();
		
		orientation = new Quaternion();
		direction = 0.0f;
		angularVelocity = 0.0f;
		angularAcceleration = 0.0f;
		
		scale = new Vector3(1.0f, 1.0f, 1.0f);
		
		drivePower = 3200.0f * 5.0f;
		turnPower = 3000.0f * 0.1f;
		
		inputArray = new byte[20];
	}
	
	public void update(float dt)
	{
		// Handle input.
		updateInput();
		
		/*
		// Reduce acceleration to zero gradually.
		acceleration.set(acceleration.x * 0.98f * dt, acceleration.y * 0.98f * dt, acceleration.z * 0.98f * dt);
		
		velocity.mulAdd(acceleration, dt);
		
		// Put some viscosity in space. Velocity in opposite direction.
		velocity.mulAdd(velocity, -0.25f * dt);
		
		position.mulAdd(velocity, dt);
		orientation.setFromAxis(0.0f, 0.0f, 1.0f, direction);
		
		angularAcceleration *= 0.98f;
		angularVelocity = angularAcceleration * dt;
		direction += angularVelocity * dt;	
		*/
		
		orientation.setFromAxis(0.0f, 0.0f, 1.0f, direction);
		
		// Update weapon.
		weapon.update(dt);
	}
	
	public void updateInput()
	{
		for (int i = 0; i < inputArray.length; i++)
		{
			inputArray[i] = 0;
		}	
		int inputArrayIdx = 0;
			
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			angularAcceleration += turnPower;
			inputArray[inputArrayIdx++] = (byte) Keys.A;
		}
		if (Gdx.input.isKeyPressed(Keys.D))
		{
			angularAcceleration += -turnPower;
			inputArray[inputArrayIdx++] = (byte) Keys.D;
		}
		if (Gdx.input.isKeyPressed(Keys.W))
		{
			acceleration.set(0.0f, drivePower, 0.0f);
			acceleration.rotate(new Vector3(0.0f, 0.0f, 1.0f), direction);
			inputArray[inputArrayIdx++] = (byte) Keys.W;
		}
		if (Gdx.input.isKeyPressed(Keys.S))
		{
			acceleration.set(0.0f, -drivePower, 0.0f);
			acceleration.rotate(new Vector3(0.0f, 0.0f, 1.0f), direction);
			inputArray[inputArrayIdx++] = (byte) Keys.S;
		}
		
		if (inputArrayIdx > 0)
		{
			// End data if array is filled with something.
			inputArray[inputArrayIdx++] = '\n';
			newInput = true;
		}
		else
		{
			newInput = false;
		}
	}
	
	public void render(OrthographicCamera camera)
	{
		// Set transformation.
		Matrix4 transform = new Matrix4(position, orientation, scale);
		batch.setTransformMatrix(transform);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, -img.getWidth() * 0.5f, -img.getHeight() * 0.5f);
		batch.end();
		
		// Render weapon.
		weapon.render(camera);
	}
	
	public byte[] getInputArray()
	{
		return inputArray;
	}
	
	public boolean getNewInput()
	{
		return newInput;
	}
	
	public void setPosition(float x, float y)
	{
		position.set(x, y, position.z);
	}
	
	public void setDirection(float d)
	{
		direction = d;
	}
}
