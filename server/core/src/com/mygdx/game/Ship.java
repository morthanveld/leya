package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

public class Ship extends Entity
{
	// Side thrust.
	private float lateralThrust;
	private float lateralThrustPower;
	
	// Forward/backward thrust.
	private float longitudinalThrust;
	private float longitudinalThrustPower;
	
	// Radial thrust.
	private float axialThrust;
	private float axialThrustPower;
	
	// Unique id per ship.
	private byte id;
	
	// Health of ship.
	private float hullIntegrity;
	
	public Ship(byte id, short type)
	{
		super(type);
		this.id = id;
		this.hullIntegrity = 100.0f;
	}

	public void updatePhysics(float dt)
	{
		Body body = super.getBody();
		
		// Apply angular movement to player.
		body.applyTorque(this.axialThrust, true);

		// Apply linear movement to player.
		Vector2 a = new Vector2(0.0f, this.longitudinalThrust);
		a.rotate(super.getDirection());
		body.applyForceToCenter(a, true);
 
		//position.set(body.getPosition());
		//orientation.set(new Vector3(0.0f, 0.0f, 1.0f), body.getAngle());
		//this.direction = body.getAngle() * 180.0f / 3.141592f;
		//System.out.println(id + " | " + connection.getInboxSize() + " | " + connection.getOutboxSize());
	}
	
	public void impact(Bullet b)
	{
		this.hullIntegrity -= 40.0f;
		
		if (this.hullIntegrity < 0.01f)
		{
			super.scheduleDestruction();
		}
	}
		
	public float getLateralThrust() {
		return lateralThrust;
	}

	public void setLateralThrust(float lateralThrust) {
		this.lateralThrust = lateralThrust;
	}

	public float getLongitudinalThrust() {
		return longitudinalThrust;
	}

	public void setLongitudinalThrust(float longitudinalThrust) {
		this.longitudinalThrust = longitudinalThrust;
	}

	public float getAxialThrust() {
		return axialThrust;
	}

	public void setAxialThrust(float axialThrust) {
		this.axialThrust = axialThrust;
	}

	public float getHullIntegrity() {
		return hullIntegrity;
	}

	public void setHullIntegrity(float hullIntegrity) {
		this.hullIntegrity = hullIntegrity;
	}


	public byte getId() {
		return id;
	}


	protected void setId(byte id) {
		this.id = id;
	}


	public float getLateralThrustPower() {
		return lateralThrustPower;
	}


	public void setLateralThrustPower(float lateralThrustPower) {
		this.lateralThrustPower = lateralThrustPower;
	}


	public float getLongitudinalThrustPower() {
		return longitudinalThrustPower;
	}


	public void setLongitudinalThrustPower(float longitudinalThrustPower) {
		this.longitudinalThrustPower = longitudinalThrustPower;
	}


	public float getAxialThrustPower() {
		return axialThrustPower;
	}


	public void setAxialThrustPower(float axialThrustPower) {
		this.axialThrustPower = axialThrustPower;
	}
}
