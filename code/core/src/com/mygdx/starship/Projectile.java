package com.mygdx.starship;

import com.badlogic.gdx.math.Vector3;

public class Projectile 
{
	public Vector3 position;
	public Vector3 velocity;
	public float life;
	
	public Projectile()
	{
		position = new Vector3();
		velocity = new Vector3();
	}
}
