package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class ProjectileManager
{
	private Array<Projectile> projectileArray;
	private int projectileId = 1;
	
	private int dirtySize = 0;
	
	private StringBuffer projectileData = null;
	
	private final Object projectileArrayLock = new Object();
	private final Object projectileDataLock = new Object();
	
	private World world = null;
	
	public ProjectileManager(World world)
	{
		this.world = world;
		synchronized (projectileArrayLock)
		{
			projectileArray = new Array<Projectile>();
		}
		
		synchronized (projectileDataLock)
		{
			projectileData = new StringBuffer();
		}
		
		// Init data.
		getProjectileData();
	}
	
	// Update movement of projectiles.
	public void updatePhysics(float dt)
	{
		dirtySize = 0;
		
		//for (Projectile p : projs)
		int i = 0;
		
		synchronized (projectileArrayLock)
		{
			while (i < projectileArray.size)
			{
				Projectile p = projectileArray.get(i);
				p.life -= dt;

				if (p.life < 0.0f)
				{
					// Projectile dead.
					//addProjectileData(p);
					p.destroy();
					projectileArray.removeIndex(i);
					continue;
				}

				/*
				Vector2 m = new Vector2(p.velocity);
				m.scl(dt);
				p.position.add(m);
				*/
				p.position.set(p.getBody().getPosition());
				
				addProjectileData(p);

				i++;

				//System.out.println("projectile-manager: " + p.position);
			}
		}
	}
	
	private void addProjectileData(Projectile p)
	{
		synchronized (projectileDataLock) 
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
	}
	
	public String getProjectileData()
	{
		String a = null;
	
		synchronized (projectileDataLock) 
		{
			// End data.
			projectileData.append("\n");

			// Get string.
			a = new String(projectileData.toString());

			// Clear data.
			projectileData.delete(0, projectileData.length());
			projectileData.append(Packet.PROJECTILE);
		}
		return a.toString();
	}
	
	public int getSize()
	{
		synchronized (projectileArrayLock)
		{
			return projectileArray.size;
		}
	}
	
	public int getDataSize()
	{
		synchronized (projectileDataLock)
		{
			return projectileData.length();
		}
	}
	
	public int getDirtySize()
	{
		return dirtySize;
	}
	
	public void create(Vector2 pos, Vector2 vel, float l)
	{	
		Projectile p = new Projectile(world, projectileId++, pos, vel, l);
		/*
		p.position.set(pos);
		p.velocity.set(vel);
		p.life = l;
		*/
		
		//addProjectileData(p);
		
		synchronized (projectileArrayLock)
		{
			projectileArray.add(p);
		}
		
		System.out.println("projectile-manager: create " + projectileId);
	}
}
