package com.mygdx.starship;

import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Space
{
	private ShapeRenderer shape;
	private Array stars;
	
	public Space()
	{
		shape = new ShapeRenderer();
		stars = new Array();
		
		Random generator = new Random(21);
		for (int i = 0; i < 100; i++)
		{
			stars.add(new Vector3((float)generator.nextDouble() * 1280.0f - 1280.0f / 2.0f, (float)generator.nextDouble() * 720.0f - 720.0f / 2.0f, 0.0f));
		}
	}

	public void update(float dt)
	{
	}
	
	public void render(OrthographicCamera camera, Ship ship)
	{
		shape.setProjectionMatrix(camera.combined);
		shape.begin(ShapeType.Point);
		shape.setColor(1, 1, 1, 1);
		
		for (int i = 0; i < stars.size; i++)
		{
			Vector3 star = (Vector3)stars.get(i);
			
			float xmin = camera.position.x - 1280.0f * 0.5f;
			float xmax = camera.position.x + 1280.0f * 0.5f;
			float ymin = camera.position.y - 720.0f * 0.5f;
			float ymax = camera.position.y + 720.0f * 0.5f;
			
			if (star.x < xmin) star.x += 1280.0f;
			if (star.x > xmax) star.x -= 1280.0f;
			if (star.y < ymin) star.y += 720.0f;
			if (star.y > ymax) star.y -= 720.0f;
			
			//float scale = 0.02f;
			//Vector3 vel = ship.velocity;
			//Vector3 dir = new Vector3(vel.x * scale, vel.y * scale, vel.z * scale); 
			/*if (dir.len() > 0.2f)
			{
				Vector3 s = new Vector3(star.x, star.y, star.z);
				Vector3 d = new Vector3(star.x - dir.x, star.y - dir.y, star.z - dir.y);
				shape.begin(ShapeType.Line);
				shape.setColor(1, 1, 1, 1);
				shape.line(s, d);
				shape.end();
			}
			else*/
			{
				
				shape.point(star.x, star.y, star.z);
				
			}
		}
		shape.end();
	}
}
