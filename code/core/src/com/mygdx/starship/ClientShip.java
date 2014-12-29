package com.mygdx.starship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ClientShip// implements InputProcessor
{
	private Vector2 position;
	private Quaternion orientation;
	private float direction;	
	private Vector3 scale;
	
	SpriteBatch batch;
	Texture img;
	
	public Weapon weapon;
	
	/*
	private byte[] inputArray = null;
	
	private int inputArrayIdx = 0;
	
	private static final int KEY_A = 0;
	private static final int KEY_W = 1;
	private static final int KEY_S = 2;
	private static final int KEY_D = 3;
	private static final int KEY_Q = 4;
	private static final int KEY_E = 5;
	
	*/
	
	private ConnectionHandler connectionHandler = null;
	//private int id = 0;
	
	//private boolean mouseButtonDown = false;
	
	public ClientShip(int id, ConnectionHandler connection)
	{
		//this.id = id;
		connectionHandler = connection;
		
		batch = new SpriteBatch();
		img = new Texture("fighter.png");
		
		weapon = new Weapon(this);
		
		position = new Vector2();
		
		orientation = new Quaternion();
		direction = 0.0f;
		
		scale = new Vector3(0.5f, 0.5f, 0.5f);
		
		/*
		inputArray = new byte[6];
		for (int i = 0; i < inputArray.length; i++)
		{
			inputArray[i] = 0;
		}
		*/	
	}
	
	public ConnectionHandler getConnectionHandler()
	{
		return this.connectionHandler;
	}
	
	public void update(float dt)
	{
		// Handle input.
		//updateInput();
				
		orientation.setFromAxis(0.0f, 0.0f, 1.0f, direction);
		
		// Update weapon.
		weapon.update(dt);
		
		/*
		if (mouseButtonDown)
		{
			createOutputPacket();
		}
		*/
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
		Vector3 p = new Vector3();
		p.set(position, 0.0f);
		Matrix4 transform = new Matrix4(p, orientation, scale);
		batch.setTransformMatrix(transform);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, -img.getWidth() * 0.5f, -img.getHeight() * 0.5f);
		batch.end();
		
		// Render weapon.
		//weapon.render(camera);
	}

	/*
	public byte[] getInputArray()
	{
		return inputArray;
	}
	
	public int getInputArraySize()
	{
		return inputArrayIdx;
	}
	*/
	
	public void setPosition(float x, float y)
	{
		position.set(x, y);
	}
	
	public void setDirection(float d)
	{
		direction = d;
	}
	
	public Weapon getWeapon()
	{
		return this.weapon;
	}
	
	public Vector2 getPosition()
	{
		return this.position;
	}

	/*
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
	
	private void createOutputPacket()
	{
		Vector2 pos = new Vector2(weapon.getWorldPosition());
		pos.add(position.x, position.y);
		
		StringBuffer a = new StringBuffer();
		a.append(this.id);		
		a.append(";");
		a.append(Packet.IO_MOUSE);
		
		a.append(";");
		a.append(pos.x);
		a.append(";");
		a.append(pos.y);
		a.append(";");
		a.append(weapon.getMouseButton());
		a.append("\n");
		
		//System.out.println("mouse " + a.toString());

		connectionHandler.addPacket(new Packet(a.toString().getBytes()));
	}

	@Override
	public boolean keyTyped(char character) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) 
	{
		weapon.setTarget(x, y);
		weapon.setWorldPosition(x - 1280.0f * 0.5f, -y + 720.0f * 0.5f);
		weapon.setMouseButton(button);
		createOutputPacket();
		mouseButtonDown = true;
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) 
	{
		weapon.setTarget(x, y);
		weapon.setWorldPosition(x - 1280.0f * 0.5f, -y + 720.0f * 0.5f);
		weapon.setMouseButton(-1);
		createOutputPacket();
		mouseButtonDown = false;
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		weapon.setTarget(x, y);
		weapon.setWorldPosition(x - 1280.0f * 0.5f, -y + 720.0f * 0.5f);
		createOutputPacket();
		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		weapon.setTarget(x, y);
		weapon.setWorldPosition(x - 1280.0f * 0.5f, -y + 720.0f * 0.5f);
		createOutputPacket();
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}*/
}
