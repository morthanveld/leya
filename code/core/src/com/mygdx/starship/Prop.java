package com.mygdx.starship;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Prop extends ClientEntity
{
	private Vector2 position;
	private float direction;
	private Vector3 scale;
	
	SpriteBatch batch;
	Texture img;
	
	private ShapeRenderer shape = null;
	
	public Prop(int id, Vector2 position, float direction)
	{
		super(id);
		
		this.position = position;
		this.direction = direction;
		this.scale = new Vector3(1.0f, 1.0f, 1.0f);
		//this.scale.scl(0.1f);
		
		batch = new SpriteBatch();
		img = new Texture("rock-1.png");		
		shape = new ShapeRenderer();
	}
	
	public void render(OrthographicCamera camera)
	{
		// Set transformation.
		Vector3 p = new Vector3(position, 0.0f);
		Quaternion q = new Quaternion(new Vector3(0.0f, 0.0f, 1.0f), direction);
		Matrix4 transform = new Matrix4(p, q, this.scale);
		
		batch.setTransformMatrix(transform);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, -img.getWidth() * 0.5f, -img.getHeight() * 0.5f);
		batch.end();
	}
	
	public float getDirection() 
	{
		return direction;
	}

	public void setDirection(float direction) 
	{
		this.direction = direction;
	}

	public Vector2 getPosition() 
	{
		return position;
	}

	public void setPosition(Vector2 position) 
	{
		this.position = position;
	}

	
}
