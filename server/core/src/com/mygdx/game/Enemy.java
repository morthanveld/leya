package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class Enemy extends Ship implements RayCastCallback
{
	private SteeringBehavior<Vector2> steeringBehavior = null;
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	private Entity target = null;
	
	private float newDirection;
	
	public Enemy()
	{
		super(Entity.ENTITY_ENEMY);

		// Setup speeds for AI.
		super.setBoundingRadius(Utils.downScale(32.0f));
		/*
		super.setMaxLinearSpeed(0.01f);
		super.setMaxLinearAcceleration(0.05f);
		super.setMaxAngularSpeed(0.1f);
		super.setMaxAngularAcceleration(0.01f);
		super.setAxialThrustPower(0.03f);
		super.setLongitudinalThrustPower(0.1f);
		super.setLateralThrustPower(0.1f);
		*/
		
		
		super.setMaxLinearSpeed(0.001f);
		super.setMaxLinearAcceleration(0.005f);
		super.setMaxAngularSpeed(0.1f);
		super.setMaxAngularAcceleration(0.01f);
		super.setAxialThrustPower(0.03f);
		super.setLongitudinalThrustPower(1.1f);
		super.setLateralThrustPower(0.1f);
	}
	
	public void createBody(Vector2 position)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.angularDamping = 0.9f;
		bodyDef.linearDamping = 0.9f;

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
		{
			avoidCollision();
			float angular = this.faceBehavior();
			super.setAxialThrust(angular * dt);
			
			super.setLongitudinalThrust(arriveBehavior() * 10.0f * dt);
		}
		
		if (steeringBehavior != null) 
		{
			// Calculate steering acceleration
			steeringBehavior.calculateSteering(steeringOutput);
			
			System.out.println(steeringOutput.linear + "\t" + steeringOutput.angular);
			
			Vector2 linear = new Vector2(steeringOutput.linear);
			float linearScale = linear.len();
			linear = linear.nor();
			
			Vector2 currentDirection = new Vector2(0.0f, 1.0f);
			currentDirection.rotateRad(getBody().getAngle());
			
			//System.out.println("linear: " + linear + "\tangular: " + steeringOutput.angular * 180.0f / 3.141592f + "\tdirection: " + currentDirection);
			
			{
				float angular = this.faceBehavior();
				super.setAxialThrust(angular * 1000.0f * dt);
			}
			
		
			//float longThrust = Math.max(0.0f, linear.dot(from) * linearScale * 500.0f * dt);
			//System.out.println(Math.max(0.0f, longThrust * linearScale));
			
			//float targetAngle = linear.angle(from.scl(-1.0f));
			//float currentAngle = getBody().getAngle() * 180.0f / 3.141592f;
			//float axialThrust = (targetAngle - currentAngle) * 0.05f * dt;
			
			//System.out.println("target: " + targetAngle + "\t" + "current: " + currentAngle + "\tlinear: " + steeringOutput.linear.angle());
					
			//super.setLongitudinalThrust(longThrust);
			//super.setAxialThrust(axialThrust);
			
			//Vector2 a = new Vector2(getBody().getTransform().getPosition());
			//a.add(steeringOutput.linear);
			//getBody().getTransform().setPosition(a);
			
			/*
			Vector2 ll = new Vector2(steeringOutput.linear);
			Vector2 dd = new Vector2(0.0f, 1.0f);
			dd.rotate(super.getDirection());
			float vv = ll.dot(dd);
			//super.setLongitudinalThrust(vv);
			//acceleration.set(0.0f, vv);

			//System.out.println("linear: " + ll + "\t" + acceleration);
			

			Vector2 linear = new Vector2(steeringOutput.linear);
			Vector2 from = new Vector2(0.0f, 1.0f);
			if (!linear.isZero(0.01f))
			{
				newDirection = from.angle(linear.nor());
			}
			
			System.out.println(steeringOutput.linear + "\t" + steeringOutput.angular);

			Vector2 dv = new Vector2(0.0f, 1.0f);
			dv.rotate(super.getDirection());

			float diff = linear.angle(dv);

			float as = super.getAngularVelocity();
			float damp = (Math.abs(diff)/360.0f);
			super.setAngularDamping(Math.max(1.0f - damp * damp * damp, 0.5f));

			//super.setAxialThrust(-diff / 45.0f * super.getMaxAngularAcceleration());
			//angularAcceleration = -diff / 45.0f * this.maxAngularAcceleration;
			
			
			
			//super.setAxialThrust(steeringOutput.angular);
			*/
			
		}
	}
	
	private float faceBehavior()
	{
		Vector2 t = target.getPosition();
		Vector2 p = this.getPosition();
		t.sub(p);
		t.nor();
		
		// Current direction vector.
		Vector2 currentDirection = new Vector2(0.0f, 1.0f);
		currentDirection.rotateRad(getBody().getAngle());
		
		float steering = t.angleRad(currentDirection) / MathUtils.PI;
		
		//System.out.println("t: " + t + "\t c: " + currentDirection + "\t r: " + steering);
		
		return -steering;
	}
	
	private float arriveBehavior()
	{
		Vector2 t = target.getPosition();
		Vector2 p = this.getPosition();
		float d = Math.max(t.sub(p).len() - 0.5f, 0.0f);
		float v = this.getLinearVelocity().len();
		
		float tv = d / 5.0f;
		
		//if (v > tv)
		//tv = Math.min(a, b)
		
		return tv;
	}
	
	private float avoidCollision()
	{
		Vector2 p = this.getPosition();
		Vector2 currentDirection = new Vector2(0.0f, 1.0f);
		currentDirection.rotateRad(getBody().getAngle());
		
		currentDirection.nor();
		currentDirection.scl(2.0f);
		
		this.world.rayCast(this, p, currentDirection);
			
		return 0.0f;
	}
	
	private float interceptBehavior()
	{
		// Interc
		return 0.0f;
	}

	public SteeringBehavior<Vector2> getSteeringBehavior() 
	{
		return steeringBehavior;
	}

	public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) 
	{
		this.steeringBehavior = steeringBehavior;
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) 
	{
		if (fixture.getBody().getUserData() != null)
		{
			Entity e = (Entity) fixture.getBody().getUserData();
			if (e.getType() == Entity.ENTITY_PLAYER)
			{
				System.out.println("player detection");
			}
		}
		else
		{
			System.out.println("collision detection");
		}
		
		return 0;
	}
}
