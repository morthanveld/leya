package com.mygdx.starship;

import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Space
{
	private ShapeRenderer shape;
	private Array<Vector3> stars;
	
	public Space()
	{
		shape = new ShapeRenderer();
		stars = new Array<Vector3>();
		
		Random generator = new Random(21);
		for (int i = 0; i < 100; i++)
		{
			stars.add(new Vector3((float)generator.nextDouble() * 1280.0f - 1280.0f / 2.0f, (float)generator.nextDouble() * 720.0f - 720.0f / 2.0f, 0.0f));
		}
	}

	public void update(float dt)
	{
	}
	
	public void render(OrthographicCamera camera, ClientShip ship)
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
			
			{
				shape.point(star.x, star.y, star.z);
			}
		}
		shape.end();
	}
}
