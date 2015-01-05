package com.mygdx.game;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemy extends Ship// implements Steerable<Vector2>
{
	private SteeringBehavior<Vector2> steeringBehavior = null;
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	private float newDirection;
	
	public Enemy()
	{
		super((byte) (int) (Math.random() * 100.0f + 1.0f), Entity.ENTITY_ENEMY);

		// Setup speeds for AI.
		super.setBoundingRadius(Utils.downScale(32.0f));
		super.setMaxLinearSpeed(0.1f);
		super.setMaxLinearAcceleration(0.3f);
		super.setMaxAngularSpeed(0.1f);
		super.setMaxAngularAcceleration(0.01f);
		
		super.setAxialThrustPower(0.03f);
		super.setLongitudinalThrustPower(0.1f);
		super.setLateralThrustPower(0.1f);
	}
	
	public void createBody(Vector2 position)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.angularDamping = 0.5f;
		bodyDef.linearDamping = 0.2f;

		CircleShape circle = new CircleShape();
		circle.setRadius(super.getBoundingRadius());

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		fixtureDef.filter.categoryBits = StarshipServer.CATEGORY_ENEMY;
		fixtureDef.filter.maskBits = StarshipServer.MASK_ENEMY;
		fixtureDef.filter.groupIndex = 0;
		
		super.createBody(bodyDef, fixtureDef);
		
		circle.dispose();
	}
	
	public void updateAi(float dt)
	{
		if (steeringBehavior != null) 
		{
			// Calculate steering acceleration
			steeringBehavior.calculateSteering(steeringOutput);

			Vector2 ll = new Vector2(steeringOutput.linear);
			Vector2 dd = new Vector2(0.0f, 1.0f);
			dd.rotate(super.getDirection());
			float vv = ll.dot(dd);
			super.setLongitudinalThrust(vv);
			//acceleration.set(0.0f, vv);

			//System.out.println("linear: " + ll + "\t" + acceleration);
			//System.out.println(steeringOutput.linear);

			Vector2 linear = new Vector2(steeringOutput.linear);
			Vector2 from = new Vector2(0.0f, 1.0f);
			newDirection = from.angle(linear);

			Vector2 dv = new Vector2(0.0f, 1.0f);
			dv.rotate(super.getDirection());

			float diff = linear.angle(dv);

			float as = super.getAngularVelocity();
			float damp = (Math.abs(diff)/360.0f);
			super.setAngularDamping(Math.max(1.0f - damp * damp * damp, 0.5f));

			super.setAxialThrust(-diff / 45.0f * super.getMaxAngularAcceleration());
			//angularAcceleration = -diff / 45.0f * this.maxAngularAcceleration;
		}
	}

	public SteeringBehavior<Vector2> getSteeringBehavior() 
	{
		return steeringBehavior;
	}

	public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) 
	{
		this.steeringBehavior = steeringBehavior;
	}
}
