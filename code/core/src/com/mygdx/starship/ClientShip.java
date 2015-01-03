package com.mygdx.starship;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ClientShip
{
	private Vector2 position;
	private Quaternion orientation;
	private float direction;	
	private Vector3 scale;
	
	SpriteBatch batch;
	Texture img;
	
	public Weapon weapon;
	
	private ConnectionHandler connectionHandler = null;
	private byte type;
	
	private static final byte ACTOR_PLAYER = 0;
	private static final byte ACTOR_AGENT = 1;
	
	public ClientShip(int id, ConnectionHandler connection)
	{
		connectionHandler = connection;
		
		batch = new SpriteBatch();
		img = new Texture("fighter.png");
		
		weapon = new Weapon(this);
		
		position = new Vector2();
		
		orientation = new Quaternion();
		direction = 0.0f;
		
		scale = new Vector3(0.5f, 0.5f, 0.5f);		
	}
	
	public ConnectionHandler getConnectionHandler()
	{
		return this.connectionHandler;
	}
	
	public void update(float dt)
	{
		orientation.setFromAxis(0.0f, 0.0f, 1.0f, direction);
		
		// Update weapon.
		weapon.update(dt);
	}
	
	public void render(OrthographicCamera camera)
	{
		// Set transformation.
		Vector3 p = new Vector3();
		p.set(position, 0.0f);
		Matrix4 transform = new Matrix4(p, orientation, scale);
		batch.setTransformMatrix(transform);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, -img.getWidth() * 0.5f, -img.getHeight() * 0.5f);
		batch.end();
	}
	
	public void setPosition(float x, float y)
	{
		position.set(x, y);
	}
	
	public void setDirection(float d)
	{
		direction = d;
	}
	
	public Weapon getWeapon()
	{
		return this.weapon;
	}
	
	public Vector2 getPosition()
	{
		return this.position;
	}

	public byte getType() 
	{
		return type;
	}

	public void setType(byte type) 
	{
		this.img.dispose();
		
		switch (type)
		{
		case ACTOR_PLAYER:
		{
			this.img = new Texture("sampleShip2.png");
			break;
		}
		case ACTOR_AGENT:
		{
			this.img = new Texture("sampleShip3.png");
			break;
		}
		}
		this.type = type;
	}
}
