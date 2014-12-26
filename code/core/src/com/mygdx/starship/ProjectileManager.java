package com.mygdx.starship;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
			shape.circle(pos.x, pos.y, 3.0f);
		}
		
		shape.end();
	}
	
	public void updatePhysics(float dt)
	{
		for (Projectile p : projs.values())
		{
			Vector2 m = new Vector2(p.velocity);
			m.scl(dt);
			
			Vector2 pos = p.getPosition();
			p.setPosition(pos.add(m));
			
			//System.out.println("projectile-manager: " + p.getPosition() + " " + p.velocity + " " + dt);
		}
	}
	
	public void clear()
	{
		projs.clear();
	}
	
	public void addProjectile(int id, float x, float y, float vx, float vy)
	{
		if (projs.containsKey(id))
		{
			Projectile p = projs.get(id);
			Vector2 pos = p.getPosition();
			pos.sub(x, y);
			
			// TODO: Offset in position between server and client could be fixed by sending bullet positions every frame.
			//System.out.println("client: projectile sync offset " + pos.len());
			
			projs.remove(id);
		}
		else
		{
			Projectile p = new Projectile(id);
			p.setPosition(x, y);
			p.setVelocity(vx, vy);
			p.setAlive(true);
			projs.put(id, p);
		}
	}
}
