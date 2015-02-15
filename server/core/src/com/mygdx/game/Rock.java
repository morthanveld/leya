package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Rock extends Prop
{
	//private int id = 0;
	
	public Rock()
	{
		super();
		//id = Utils.getUniqueId();
	}
	
	public void createBody(Vector2 position)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.angularDamping = 0.5f;
		bodyDef.linearDamping = 0.2f;

		/*
		int c = 5;
		Vector2[] vertices = new Vector2[c];
		for (int i = 0; i < c; i++)
		{
			Vector2 r = new Vector2(Utils.getNextRandom(), Utils.getNextRandom());
			r.scl(2.0f);
			r.sub(1.0f, 1.0f);
			r.nor();
			r.scl(2.0f);
			r.add(position);
			
			vertices[i] = new Vector2(r);
		}
		
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		*/
		
		CircleShape shape1 = new CircleShape();
		shape1.setPosition(Utils.downScale(new Vector2(-64.0f, -64.0f)));
		shape1.setRadius(Utils.downScale(128.0f));
		
		CircleShape shape2 = new CircleShape();
		shape2.setPosition(Utils.downScale(new Vector2(64.0f, 64.0f)));
		shape2.setRadius(Utils.downScale(128.0f));
		
//		System.out.println(shape.getVertexCount());

		FixtureDef fixtureDef1 = new FixtureDef();
		fixtureDef1.shape = shape1;
		fixtureDef1.density = 0.5f; 
		fixtureDef1.friction = 0.4f;
		fixtureDef1.restitution = 0.6f;
		fixtureDef1.filter.categoryBits = StarshipServer.CATEGORY_ENEMY;
		fixtureDef1.filter.maskBits = StarshipServer.MASK_ENEMY;
		fixtureDef1.filter.groupIndex = 0;
		
		FixtureDef fixtureDef2 = new FixtureDef();
		fixtureDef2.shape = shape2;
		fixtureDef2.density = 0.5f; 
		fixtureDef2.friction = 0.4f;
		fixtureDef2.restitution = 0.6f;
		fixtureDef2.filter.categoryBits = StarshipServer.CATEGORY_ENEMY;
		fixtureDef2.filter.maskBits = StarshipServer.MASK_ENEMY;
		fixtureDef2.filter.groupIndex = 0;
		
		super.createBody(bodyDef, fixtureDef1);
		super.createBody(bodyDef, fixtureDef2);
		
		shape1.dispose();
		shape2.dispose();
	}
	
	/*
	public int getId()
	{
		return this.id;
	}
	*/
}
