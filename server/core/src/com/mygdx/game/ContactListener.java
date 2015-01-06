package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener
{
	private World world = null;
	private StarshipServer server = null;
	
	public ContactListener(StarshipServer ss)
	{
		world = WorldSingleton.getInstance().getWorld();
		world.setContactListener(this);
		server = ss;
	}

	@Override
	public void beginContact(Contact contact) 
	{
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		
		if (bodyA != null && bodyB != null)
		{
			Entity a = (Entity) bodyA.getUserData();
			Entity b = (Entity) bodyB.getUserData();
			
			if (a != null && b != null)
			{
				if (a instanceof Ship && b instanceof Bullet)
				{
					((Ship) a).impact((Bullet) b);
					((Bullet) b).scheduleDestruction();
				}
				else if (b instanceof Ship && a instanceof Bullet)
				{
					((Ship) b).impact((Bullet) a);
					((Bullet) a).scheduleDestruction();					
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) 
	{
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		// TODO Auto-generated method stub		
	}

}
