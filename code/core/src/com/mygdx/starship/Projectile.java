package com.mygdx.starship;

import com.badlogic.gdx.math.Vector2;

public class Projectile 
{
	private Vector2 position;
	private Vector2 oldPosition;
	public Vector2 velocity;
	public float life;
	public int id;
	
	private boolean isAlive = false;
	
	public Projectile(int id)
	{
		position = new Vector2(0.0f, 0.0f);
		oldPosition = new Vector2(0.0f, 0.0f);
		velocity = new Vector2();
		life = 0.0f;
		this.id = id;
		this.isAlive = true;
	}
	
	public void setPosition(Vector2 p)
	{
		oldPosition.set(position);
		position.set(p);
	}
	
	public void setPosition(float x, float y)
	{
		oldPosition.set(position);
		position.set(x, y);
	}
	
	public Vector2 getPosition()
	{
		return position;
	}
	
	public Vector2 getOldPosition()
	{
		return oldPosition;
	}
	
	public void setVelocity(float x, float y)
	{
		velocity.set(x, y);
	}
	
	public void setVelocity(Vector2 v)
	{
		velocity.set(v);
	}
	
	public void setAlive(boolean a)
	{
		isAlive = a;
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
}
