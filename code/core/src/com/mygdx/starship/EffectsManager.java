package com.mygdx.starship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class EffectsManager 
{
	private SpriteBatch batch;
	
	private Array<ParticleEffect> pe = null;
	private Vector3 scale;
	
	
	public EffectsManager()
	{
		batch = new SpriteBatch();
		
		pe = new Array<ParticleEffect>();
		
		for (int i = 0; i < 10; i++)
		{
			pe.add(new ParticleEffect());
			pe.peek().load(Gdx.files.internal("test.ps"), Gdx.files.internal(""));
		}
		
		scale = new Vector3(1.0f, 1.0f, 1.0f);
	}
	
	public void update(float dt)
	{
		for (ParticleEffect p : pe)
		{
			p.update(dt);
		}
	}
	
	public void render(OrthographicCamera camera)
	{
		Vector3 pos = new Vector3();
		Matrix4 transform = new Matrix4(pos, new Quaternion(), scale);
		
		batch.setTransformMatrix(transform);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (ParticleEffect p : pe)
		{
			p.draw(batch);
		}

		batch.end();
	}
	
	public void createParticleEffect(int type, Vector2 position)
	{
		for (ParticleEffect p : pe)
		{
			// Find first completed particle system.
			if (p.isComplete())
			{
				p.getEmitters().first().setPosition(position.x, position.y);
				p.start();
				
				return;
			}
		}
		
		Gdx.app.log("client-effectsmanager", "Not enough allocated particle systems.");
	}
	
}
