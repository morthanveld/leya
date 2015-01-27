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
	public Rock()
	{
		super();
	}
	
	public void createBody(Vector2 position)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.angularDamping = 0.5f;
		bodyDef.linearDamping = 0.2f;

		int c = 4;
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
		
		System.out.println(shape.getVertexCount());

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		fixtureDef.filter.categoryBits = StarshipServer.CATEGORY_ENEMY;
		fixtureDef.filter.maskBits = StarshipServer.MASK_ENEMY;
		fixtureDef.filter.groupIndex = 0;
		
		super.createBody(bodyDef, fixtureDef);
		
		shape.dispose();
	}
}
