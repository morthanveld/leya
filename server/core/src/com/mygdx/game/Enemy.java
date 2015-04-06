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
	
	private Vector2 rayHitNormal = null;
	private Vector2 rayHitPoint = null;
	
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
		Vector2 position = this.getPosition();
		
		// Prepare ray cast in front of ship.
		float rayLength = 2.0f;
		float rayFov = (float) Math.PI * 0.25f;
		Vector2 ray = new Vector2(0.0f, 1.0f);
		ray.rotateRad(getBody().getAngle() + ((float) Math.random() * 2.0f - 1.0f) * rayFov);
		ray.nor();
		ray.scl(rayLength);
		
		Vector2 rayTarget = new Vector2(position);
		rayTarget.add(ray);
		
		// Cast ray.
		if (this.rayHitNormal == null)
		{
			this.world.rayCast(this, position, rayTarget);
		}
		
		// Calculate speed, arrives on target.
		float stopAtDistance = 0.0f;
		float slowDownDistance = 1.0f;
		Vector2 targetPosition = new Vector2(target.getPosition());
		float d = Math.max(targetPosition.sub(position).len() - stopAtDistance, 0.0f);
		float speed = d / slowDownDistance;
		
		// Ray hits something.
		if (this.rayHitNormal != null)
		{
			Vector2 rayHitPosition = new Vector2(this.rayHitPoint);
			rayHitPosition.sub(position);
			
			float distance = rayHitPosition.len();
		
			// Avoid object by steer in opposite direction.
			Vector2 wing = new Vector2(1.0f, 0.0f);
			wing.rotateRad(getBody().getAngle());
			wing.nor();
			
			float v = 1.0f - this.rayHitNormal.dot(wing);
			
			this.rayHitNormal = null;
			this.rayHitPoint = null;
			
			super.setAxialThrust(v * dt);
			super.setLongitudinalThrust(distance / rayLength * 0.2f);
		}
		else
		{
			targetPosition = new Vector2(target.getPosition());
			targetPosition.sub(position);
			targetPosition.nor();
			
			// Current direction vector.
			Vector2 currentDirection = new Vector2(0.0f, 1.0f);
			currentDirection.rotateRad(getBody().getAngle());
			
			float steering = targetPosition.angleRad(currentDirection) / MathUtils.PI * -1.0f;
			
			//System.out.println("t: " + t + "\t c: " + currentDirection + "\t r: " + steering);
		
			super.setAxialThrust(steering * dt);		
			super.setLongitudinalThrust(speed * 10.0f * dt);
		}
	}
	/*
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
		currentDirection.rotateRad(getBody().getAngle() + ((float) Math.random() * 2.0f - 1.0f) * (float) Math.PI * 0.5f);
		
		currentDirection.nor();
		currentDirection.scl(2.0f);
		
		Vector2 target = new Vector2(p);
		target.add(currentDirection);
		
		if (this.rayHitNormal == null)
		{
			this.world.rayCast(this, p, target);
		}
		
		if (this.rayHitNormal != null)
		{
			Vector2 d = new Vector2(this.rayHitPoint);
			d.sub(p);
			
			float distance = d.len();
			System.out.println("distance: " + distance);
			
			// Avoid object by steer in opposite direction.
			Vector2 wing = new Vector2(1.0f, 0.0f);
			wing.rotateRad(getBody().getAngle());
			wing.nor();
			
			float v = this.rayHitNormal.dot(wing);
			
			this.rayHitNormal = null;
			this.rayHitPoint = null;
			
			return 1.0f - v;
		}
			
		return 0.0f;
	}
	
	private float interceptBehavior()
	{
		// Interc
		return 0.0f;
	}
	*/

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
			/*Entity e = (Entity) fixture.getBody().getUserData();
			if (e.getType() == Entity.ENTITY_PLAYER)
			{
				System.out.println("player detection");
			}
			else*/
			{
				System.out.println("collision detection");
				this.rayHitNormal = new Vector2(normal);
				this.rayHitPoint = new Vector2(point);
			}
		}
		
		return 0;
	}
}
