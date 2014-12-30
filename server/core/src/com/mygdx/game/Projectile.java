package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

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
	private Body body = null;
	private World world = null;
	
	public Projectile(World world, int id, Vector2 position, Vector2 velocity, float life)
	{
		this.position = new Vector2(position);
		this.velocity = new Vector2(velocity);
		this.life = life;
		this.id = id;
		this.world = world;

		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(this.position);
		bodyDef.linearVelocity.set(this.velocity);
		bodyDef.fixedRotation = true;

		// Create our body in the world using our body definition
		body = world.createBody(bodyDef);

		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(6.0f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit

		// Create our fixture and attach it to the body
		Fixture fixture = body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
	}
	
	public Body getBody()
	{
		return this.body;
	}
	
	public void destroy()
	{
		world.destroyBody(this.body);
	}
}
