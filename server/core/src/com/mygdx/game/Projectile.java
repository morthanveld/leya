package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Projectile 
{
	/*
	public static final int NEW = 0x0;
	public static final int LIVING = 0x1;
	public static final int DEAD = 0x2;
	public static final int DELETE = 0x3;
	*/
	
	public Vector2 position;
	public Vector2 velocity;
	public float life;
	public int id;
	//public boolean dirty = false;
	
//	public int state = 0;
	
	public Projectile(int id)
	{
		position = new Vector2();
		velocity = new Vector2();
		life = 0.0f;
		this.id = id;
	}
}
