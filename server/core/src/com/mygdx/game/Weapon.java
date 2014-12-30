package com.mygdx.game;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Weapon 
{
	//private Ship ship;
	private float timer = 0.0f;
	private Vector2 target;
	//private Vector2 targetDirection;
	//private ShapeRenderer shape;
	private float cooldown; //ms
	
	//private Array<Projectile> projectiles;
	
	private float power;
	private float projectileLife;
	
	private Player player = null;
	
	public Weapon(Player p)
	{
		player = p;
		target = new Vector2();
		//targetDirection = new Vector2();
		//shape = new ShapeRenderer();
		cooldown = 0.5f;
		timer = 0.0f;
		power = 100.0f * 10.0f;
		projectileLife = 10.0f;
		
		//projectiles = new Array<Projectile>();
	}
	
	void update(float dt)
	{
		// Reduce cooldown timer.
		timer = timer - dt;
		
		//updateProjectiles(dt);
	}
	
	/*
	void updateProjectiles(float dt)
	{
		// Update projectiles.
		for (int i = 0; i < projectiles.size; i++)
		{
			Projectile p = (Projectile)projectiles.get(i);
			Vector2 pos = p.position.cpy();
			Vector2 vel = p.velocity.cpy();
			vel.scl(dt);
			pos.add(vel);

			p.position.set(pos);
			p.life -= dt;

			if (p.life < 0.0f)
			{
				// Remove projectile if life is 0.
				projectiles.removeIndex(i);
			}
			else
			{
				// Update projectile data.
				projectiles.set(i, p);
			}
		}
	}
	*/
		
	void fire()
	{
		if (timer <= 0.0f)
		{
			// Calculate direction.
			Vector2 dir = target.sub(player.getPosition());
			dir.nor();
			dir.scl(power);
			dir.add(player.getVelocity());
			
			/*
			// Create new projectile.
			Projectile p = new Projectile();
			p.position.set(player.getPosition());
			p.velocity.set(dir.x * power, dir.y * power);
			p.life = projectileLife;
			*/
			
			//System.out.println("New Projectile - Power: " + power + " Life: " + projectileLife);
			
			//projectiles.add(p);
			
			player.getProjectileManager().create(player.getPosition(), dir, projectileLife);

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
