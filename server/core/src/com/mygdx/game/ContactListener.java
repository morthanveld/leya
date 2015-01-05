package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener
{
	public ContactListener()
	{
		
	}

	@Override
	public void beginContact(Contact contact) 
	{
		//contact.getFixtureA().getBody().getUserData()
		
		// get entity
		// determine type, ship, bullet, asteroid
		// if ship and bullet, reduce ship health with bullet energy, kill bullet
		// if ship and world object, calculate health penalty based on impact force.
		contact.getFixtureA().getBody().getLinearVelocity();
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
