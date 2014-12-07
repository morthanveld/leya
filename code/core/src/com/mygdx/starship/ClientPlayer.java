package com.mygdx.starship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class ClientPlayer implements InputProcessor
{
	public Vector3 position;
	private Quaternion orientation;
	private float direction;	
	private Vector3 scale;
	
	SpriteBatch batch;
	Texture img;
	
	public Weapon weapon;
	
	private byte[] inputArray = null;
	
	private int inputArrayIdx = 0;
	
	private static final int KEY_A = 0;
	private static final int KEY_W = 1;
	private static final int KEY_S = 2;
	private static final int KEY_D = 3;
	private static final int KEY_Q = 4;
	private static final int KEY_E = 5;
	
	public ClientPlayer()
	{
		batch = new SpriteBatch();
		img = new Texture("fighter.png");
		
		weapon = new Weapon(this);
		
		position = new Vector3();
		
		orientation = new Quaternion();
		direction = 0.0f;
		
		scale = new Vector3(1.0f, 1.0f, 1.0f);
		
		inputArray = new byte[6];
		for (int i = 0; i < inputArray.length; i++)
		{
			inputArray[i] = 0;
		}	
	}
	
	public void update(float dt)
	{
		// Handle input.
		//updateInput();
				
		orientation.setFromAxis(0.0f, 0.0f, 1.0f, direction);
		
		// Update weapon.
		weapon.update(dt);
	}
	
	public void updateInput()
	{
		/*
		for (int i = 0; i < inputArray.length; i++)
		{
			inputArray[i] = 0;
		}	
		inputArrayIdx = 0;
			
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			inputArray[inputArrayIdx++] = (byte) Keys.A;
		}
		if (Gdx.input.isKeyPressed(Keys.D))
		{
			inputArray[inputArrayIdx++] = (byte) Keys.D;
		}
		if (Gdx.input.isKeyPressed(Keys.W))
		{
			inputArray[inputArrayIdx++] = (byte) Keys.W;
		}
		if (Gdx.input.isKeyPressed(Keys.S))
		{
			inputArray[inputArrayIdx++] = (byte) Keys.S;
		}
		
		if (inputArrayIdx > 0)
		{
			// End data if array is filled with something.
			inputArray[inputArrayIdx++] = '\n';
		}*/
	}
	
	public void render(OrthographicCamera camera)
	{
		// Set transformation.
		Matrix4 transform = new Matrix4(position, orientation, scale);
		batch.setTransformMatrix(transform);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, -img.getWidth() * 0.5f, -img.getHeight() * 0.5f);
		batch.end();
		
		// Render weapon.
		//weapon.render(camera);
	}
	
	public byte[] getInputArray()
	{
		return inputArray;
	}
	
	public int getInputArraySize()
	{
		return inputArrayIdx;
	}
	
	public void setPosition(float x, float y)
	{
		position.set(x, y, position.z);
	}
	
	public void setDirection(float d)
	{
		direction = d;
	}

	@Override
	public boolean keyDown(int keycode) 
	{
		switch (keycode)
		{
		case Keys.A:
		{
			inputArray[KEY_A] = 1;
			return true;
		}
		case Keys.W:
		{
			inputArray[KEY_W] = 1;
			return true;
		}
		case Keys.S:
		{
			inputArray[KEY_S] = 1;
			return true;
		}
		case Keys.D:
		{
			inputArray[KEY_D] = 1;
			return true;
		}
		case Keys.Q:
		{
			inputArray[KEY_Q] = 1;
			return true;
		}
		case Keys.E:
		{
			inputArray[KEY_E] = 1;
			return true;
		}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) 
	{
		switch (keycode)
		{
		case Keys.A:
		{
			inputArray[KEY_A] = 0;
			return true;
		}
		case Keys.W:
		{
			inputArray[KEY_W] = 0;
			return true;
		}
		case Keys.S:
		{
			inputArray[KEY_S] = 0;
			return true;
		}
		case Keys.D:
		{
			inputArray[KEY_D] = 0;
			return true;
		}
		case Keys.Q:
		{
			inputArray[KEY_Q] = 0;
			return true;
		}
		case Keys.E:
		{
			inputArray[KEY_E] = 0;
			return true;
		}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
