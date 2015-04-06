package com.mygdx.starship;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ProjectileManager
{
	private ShapeRenderer shape = null;
	private Array<Vector2> projectileArray = null;
	
	public ProjectileManager()
	{
		shape = new ShapeRenderer();
		projectileArray = new Array<Vector2>(200);
	}
	
	public void render(Camera camera)
	{
		shape.setProjectionMatrix(camera.combined);

		// Draw projectiles.
		shape.begin(ShapeType.Filled);
		shape.setColor(1, 0, 0, 1);
		
		Vector2 v = null;
		for (int i = 0; i < projectileArray.size; i++)
		{
			v = projectileArray.get(i);
			shape.circle(v.x, v.y, 3.0f);
		}
			
		shape.end();
	}
	
	public void updatePhysics(float dt)
	{
		
	}
	
	public void clear()
	{
		projectileArray.clear();
	}
	
	public void addProjectile(int id, float x, float y, float vx, float vy)
	{
		projectileArray.add(new Vector2(x, y));
	}
}
