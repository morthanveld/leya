package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Entity implements Steerable<Vector2>
{
	public static int ENTITY_WORLD = 1;
	public static int ENTITY_PLAYER = 2;
	public static int ENTITY_ENEMY = 3;
	public static int ENTITY_BULLET = 4;
	public static int ENTITY_PROP = 5;
	
	private int type = 0;
	private Body body = null;
	private World world = null;
	
	private boolean tagged = false;
	private float boundingRadius = 0.0f;
	private float maxLinearSpeed = 0.1f; //320000.0f;
	private float maxLinearAcceleration = 0.3f; //320000.0f;
	private float maxAngularSpeed = 0.1f; //100000.0f;
	private float maxAngularAcceleration = 0.01f; //3000000.0f;
	
	private boolean scheduleDestruction = false;
	
	private int id = 0;
	
	// TODO: MUST FIX DESTROY OF ENTITY
	public Entity(int type)
	{
		if (type != ENTITY_PLAYER)
		{
			// If entity is a player, id get set later.
			this.id = Utils.getUniqueId();
		}
		this.type = type;
		this.world = WorldSingleton.getInstance().getWorld();
	}
	
	
	public int getId()
	{
		return this.id;
	}
	
	protected void setId(int id)
	{
		this.id = id;
	}
	
	public void destroy()
	{
		if (body != null)
		{
			this.world.destroyBody(this.body);
		}
	}
	
	public void scheduleDestruction()
	{
		this.scheduleDestruction = true;
	}
	
	public boolean isScheduledDestruction()
	{
		return this.scheduleDestruction;
	}
	
	public void createBody(BodyDef bodyDef, FixtureDef fixtureDef)
	{
		if (body == null)
		{
			this.body = this.world.createBody(bodyDef);
			this.body.createFixture(fixtureDef);
			this.body.setUserData(this);
		}
		else
		{
			//System.err.println("Entity: Body is not null.");
			Gdx.app.log("entity-server", "body already created, adding another fixturedef");
			this.body.createFixture(fixtureDef);
		}
	}
	
	public Body getBody()
	{
		return this.body;
	}

	public int getType()
	{
		return this.type;
	}
	
	public Vector2 getPosition()
	{
		if (body != null)
		{
			return this.body.getPosition();
		}
		
		return null;
	}
	
	public Vector2 getLinearVelocity()
	{
		if (body != null)
		{
			return this.body.getLinearVelocity();
		}
		
		return null;
	}
	
	public float getDirection()
	{
		if (body != null)
		{
			return this.body.getAngle() * MathUtils.radiansToDegrees;
		}
		
		return 0.0f;
	}
		
	public float getAngularVelocity()
	{
		if (body != null)
		{
			return this.body.getAngularVelocity();
		}
		
		return 0.0f;
	}
	
	public void setAngularDamping(float damping)
	{
		if (body != null)
		{
			body.setAngularDamping(damping);
		}
	}
	
	public void setLinearDamping(float damping)
	{
		if (body != null)
		{
			body.setLinearDamping(damping);
		}
	}
	
	public float getMaxLinearSpeed() 
	{
		return this.maxLinearSpeed;
	}

	public void setMaxLinearSpeed(float maxLinearSpeed) 
	{
		this.maxLinearSpeed = maxLinearSpeed;
	}

	public float getMaxLinearAcceleration() 
	{
		return this.maxLinearAcceleration;
	}

	public void setMaxLinearAcceleration(float maxLinearAcceleration) 
	{
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	public float getMaxAngularSpeed() 
	{
		return this.maxAngularSpeed;
	}

	public void setMaxAngularSpeed(float maxAngularSpeed) 
	{
		this.maxAngularSpeed = maxAngularSpeed;
	}

	public float getMaxAngularAcceleration() 
	{
		return this.maxAngularAcceleration;
	}

	public void setMaxAngularAcceleration(float maxAngularAcceleration) 
	{
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	
	public float getOrientation() 
	{
		return getDirection();
	}
	
	
	protected void setBoundingRadius(float boundingRadius)
	{
		this.boundingRadius = boundingRadius;
	}

	public float getBoundingRadius() 
	{
		return this.boundingRadius;
	}

	public boolean isTagged() 
	{
		return this.tagged;
	}

	public void setTagged(boolean tagged) 
	{
		this.tagged = tagged;
	}

	public Vector2 newVector() 
	{
		return new Vector2();
	}

	public float vectorToAngle(Vector2 vector) 
	{
		return (float)Math.atan2(-vector.x, vector.y);
	}

	public Vector2 angleToVector(Vector2 outVector, float angle) 
	{
		outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
	}
}

