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
		
		// Setup speeds for AI.
		super.setBoundingRadius(Utils.downScale(64.0f * 3.0f));
		super.setMaxLinearSpeed(0.0f);
		super.setMaxLinearAcceleration(0.0f);
		super.setMaxAngularSpeed(0.0f);
		super.setMaxAngularAcceleration(0.0f);
	}
	
	public void createBody(Vector2 position)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.angularDamping = 0.5f;
		bodyDef.linearDamping = 0.2f;
		
		CircleShape shape1 = new CircleShape();
		shape1.setPosition(Utils.downScale(new Vector2(-64.0f, -64.0f)));
		shape1.setRadius(Utils.downScale(128.0f));
		
		CircleShape shape2 = new CircleShape();
		shape2.setPosition(Utils.downScale(new Vector2(64.0f, 64.0f)));
		shape2.setRadius(Utils.downScale(128.0f));
	
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
}
