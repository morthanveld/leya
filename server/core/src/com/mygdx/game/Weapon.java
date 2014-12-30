package com.mygdx.game;


import com.badlogic.gdx.math.Vector2;

public class Weapon 
{
	private float timer = 0.0f;
	private Vector2 target;
	private float cooldown; //ms
	
	private float power;
	private float projectileLife;
	
	private Player player = null;
	
	private Vector2 weaponOffset = null;
	
	public Weapon(Player p)
	{
		player = p;
		target = new Vector2();
		cooldown = 0.5f;
		timer = 0.0f;
		power = 100.0f * 10.0f;
		projectileLife = 10.0f;
		
		weaponOffset = new Vector2(0.0f, 32.0f);
	}
	
	void update(float dt)
	{
		// Reduce cooldown timer.
		timer = timer - dt;
	}
		
	void fire()
	{
		if (timer <= 0.0f)
		{
			// Calculate direction.
			Vector2 weaponOffsetWorld = new Vector2(weaponOffset);
			weaponOffsetWorld.rotate(player.getDirection());
			Vector2 weaponPosition = new Vector2(player.getPosition());
			weaponPosition.add(weaponOffsetWorld);
			
			Vector2 dir = target.sub(weaponPosition);
			dir.nor();
			dir.scl(power);
			dir.add(player.getVelocity());
			
			// Create projectile.
			player.getProjectileManager().create(weaponPosition, dir, projectileLife);

			// Reset weapon cooldown.
			timer = cooldown;
		}
	}
	
	// In world space.
	public void setTarget(Vector2 target)
	{
		this.target = target;
	}
	
	public void setTarget(float x, float y)
	{
		this.target.set(x, y);
	}
	
	public Vector2 getTarget()
	{
		return this.target;
	}
}
