package com.mygdx.starship;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class ProjectileManager
{
	private HashMap<Integer, Projectile> projs;
	private ShapeRenderer shape = null;
	
	public ProjectileManager()
	{
		projs = new HashMap<Integer, Projectile>();
		shape = new ShapeRenderer();
	}
	
	public void render(Camera camera)
	{
		shape.setProjectionMatrix(camera.combined);

		// Draw projectiles.
		shape.begin(ShapeType.Filled);
		shape.setColor(1, 0, 0, 1);
		
		for (Projectile p : projs.values())
		{
			Vector2 pos = p.getPosition();
			//shape.circle(pos.x, pos.y, 3.0f);
			shape.line(p.getOldPosition(), pos);
		}
		
		shape.end();
	}
	
	public void updatePhysics(float dt)
	{
	}
	
	public void clear()
	{
		projs.clear();
	}
	
	public void addProjectile(int id, float x, float y, float vx, float vy)
	{
		if (projs.containsKey(id))
		{
			// Update projectile.
			Projectile p = projs.get(id);
			p.setPosition(x, y);
			p.setAlive(true);
		}
		else
		{
			// Create new projectile.
			Projectile p = new Projectile(id);
			p.setPosition(x, y);
			p.setVelocity(vx, vy);
			p.setAlive(true);
			projs.put(id, p);
		}
	}
}
