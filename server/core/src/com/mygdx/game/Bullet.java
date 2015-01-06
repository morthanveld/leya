package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Bullet extends Entity
{
	/*
	public Vector2 position;
	public Vector2 velocity;
	*/
	private float life;
	private int id;

	private Body body = null;
	private World world = null;
	
	public Bullet(World world, int id)//, Vector2 position, Vector2 velocity, float life)
	{
		super(ENTITY_BULLET);
		
		/*
		this.position = new Vector2(position);
		this.velocity = new Vector2(velocity);
		this.life = life;
		*/
		this.id = id;
		this.world = world;
		this.life = 5.0f;
		
	}
	
	public void createBody(Vector2 position, Vector2 velocity)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.linearVelocity.set(velocity);
		bodyDef.fixedRotation = true;

		CircleShape circle = new CircleShape();
		circle.setRadius(Utils.downScale(6.0f));

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		fixtureDef.filter.categoryBits = StarshipServer.CATEGORY_BULLET;
		fixtureDef.filter.maskBits = StarshipServer.MASK_BULLET;
		fixtureDef.filter.groupIndex = 0;

		super.createBody(bodyDef, fixtureDef);
		
		circle.dispose();
	}
	
	public void update(float dt)
	{
		life -= dt;
	}
	
	public int getId()
	{
		return this.id;
	}
		
	public boolean isDead()
	{
		return (life < 0.0f);
	}
	
	
	/*
	public Body getBody()
	{
		return this.body;
	}
	
	public void destroy()
	{
		world.destroyBody(this.body);
	}*/
}
