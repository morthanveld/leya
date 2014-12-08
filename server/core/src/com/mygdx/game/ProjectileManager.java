package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ProjectileManager
{
	private Array<Projectile> projs;
	private int projectileId = 1;
	
	private int dirtySize = 0;
	
	private StringBuffer projectileData = null;
	
	public ProjectileManager()
	{
		projs = new Array<Projectile>();
		projectileData = new StringBuffer();
		
		// Init data.
		getProjectileData();
	}
	
	// Update movement of projectiles.
	public void updatePhysics(float dt)
	{
		dirtySize = 0;
		
		//for (Projectile p : projs)
		int i = 0;
		while (i < projs.size)
		{
			Projectile p = projs.get(i);
			p.life -= dt;
						
			if (p.life < 0.0f)
			{
				// Projectile dead.
				addProjectileData(p);
				projs.removeIndex(i);
				continue;
			}
			
			Vector2 m = new Vector2(p.velocity);
			m.scl(dt);
			p.position.add(m);
			
			i++;
					
			//System.out.println("projectile-manager: " + p.position);
		}
	}
	
	private void addProjectileData(Projectile p)
	{
		projectileData.append(";");
		projectileData.append(p.id);
		projectileData.append(";");
		projectileData.append(p.position.x);
		projectileData.append(";");
		projectileData.append(p.position.y);
		projectileData.append(";");
		projectileData.append(p.velocity.x);
		projectileData.append(";");
		projectileData.append(p.velocity.y);
	}
	
	public String getProjectileData()
	{
		// End data.
		projectileData.append("\n");
		
		// Get string.
		String a = projectileData.toString();
		
		// Clear data.
		projectileData.delete(0, projectileData.length());
		projectileData.append(Packet.PROJECTILE);
		
		
		/*
		StringBuffer a = new StringBuffer();
		a.append(Packet.PROJECTILE);
		
		for (Projectile p : projs)
		{
			if (p.state == Projectile.NEW)
			{
				a.append(";");
				a.append(p.id);
				a.append(";");
				a.append(p.position.x);
				a.append(";");
				a.append(p.position.y);
				a.append(";");
				a.append(p.velocity.x);
				a.append(";");
				a.append(p.velocity.y);
				
				p.state = Projectile.LIVING;
			}
			else if (p.state == Projectile.DEAD)
			{
				a.append(";");
				a.append(p.id);
				a.append(";");
				a.append(p.position.x);
				a.append(";");
				a.append(p.position.y);
				a.append(";");
				a.append(p.velocity.x);
				a.append(";");
				a.append(p.velocity.y);
				
				p.state = Projectile.DELETE;
			}
		}
		*/
		
		//a.append("\n");
		return a.toString();
	}
	
	public int getSize()
	{
		return projs.size;
	}
	
	public int getDataSize()
	{
		return projectileData.length();
	}
	
	public int getDirtySize()
	{
		return dirtySize;
	}
	
	public void create(Vector2 pos, Vector2 vel, float l)
	{
		Projectile p = new Projectile(projectileId++);
		p.position.set(pos);
		p.velocity.set(vel);
		p.life = l;
		
		addProjectileData(p);
		
		projs.add(p);
		
		System.out.println("projectile-manager: create");
	}
}
