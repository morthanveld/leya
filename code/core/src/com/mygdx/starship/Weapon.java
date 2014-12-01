package com.mygdx.starship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Weapon 
{
	private Ship ship;
	private float timer;
	private Vector2 target;
	private Vector2 targetDirection;
	private ShapeRenderer shape;
	private float cooldown; //ms
	
	private Array<Projectile> projectiles;
	
	private float power;
	private float projectileLife;
	
	private byte[] inputArray = null;
	private boolean newInput = false;
	
	public Weapon(Ship s)
	{
		ship = s;
		target = new Vector2();
		targetDirection = new Vector2();
		shape = new ShapeRenderer();
		cooldown = 0.01f;
		timer = 0.0f;
		power = 1000.0f;
		projectileLife = 2.0f;
		
		projectiles = new Array<Projectile>();
		
		inputArray = new byte[20];
	}
	
	void update(float dt)
	{
		// Reduce cooldown timer.
		timer = timer - dt;
		
		updateInput(dt);
		updateProjectiles(dt);
	}
	
	void updateProjectiles(float dt)
	{
		// Update projectiles.
		for (int i = 0; i < projectiles.size; i++)
		{
			Projectile p = (Projectile)projectiles.get(i);
			Vector3 pos = p.position.cpy();
			Vector3 vel = p.velocity.cpy();
			vel.scl(dt);
			pos.add(vel);

			p.position.set(pos);
			p.life -= dt;

			if (p.life < 0.0f)
			{
				// Remove projectile if life is 0.
				projectiles.removeIndex(i);
			}
			else
			{
				// Update projectile data.
				projectiles.set(i, p);
			}
		}
	}
	
	void updateInput(float dt)
	{
		// Capture mouse position.
		target.set(Gdx.input.getX(), Gdx.input.getY());
		targetDirection.set(target.x - 1280.0f * 0.5f, -target.y + 720.0f * 0.5f);
		targetDirection.nor();
		
		// Put input in input array.
		for (int i = 0; i < inputArray.length; i++)
		{
			inputArray[i] = 0;
		}	
		int inputArrayIdx = 0;
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
		{
			fire(dt);
			inputArray[inputArrayIdx++] = (byte) Input.Buttons.LEFT;
		}
		
		final int lastX = 0;
		final int lastY = 0;
		int x = Gdx.input.getX();
		int y = Gdx.input.getY();
		
		if (Math.abs(lastX - x) < 1 || Math.abs(lastY - y) < 1)
		{
			// Update input array if mouse position differs from last frame.
			byte[] b = floatToBytes(targetDirection.x);
			inputArray[inputArrayIdx++] = b[0];
			inputArray[inputArrayIdx++] = b[1];
			inputArray[inputArrayIdx++] = b[2];
			inputArray[inputArrayIdx++] = b[3];
			
			b = floatToBytes(targetDirection.y);
			inputArray[inputArrayIdx++] = b[0];
			inputArray[inputArrayIdx++] = b[1];
			inputArray[inputArrayIdx++] = b[2];
			inputArray[inputArrayIdx++] = b[3];
		}
		
		if (inputArrayIdx > 0)
		{
			// End data if array is filled with something.
			inputArray[inputArrayIdx++] = '\n';
			newInput = true;
		}
		else
		{
			newInput = false;
		}
	}
	
	void fire(float dt)
	{
		if (timer <= 0.0f)
		{
			// Create new projectile.
			Projectile p = new Projectile();
			p.position.set(ship.position.x, ship.position.y, ship.position.z);
			p.velocity.set(targetDirection.x * power, targetDirection.y * power, 0.0f);
			p.life = projectileLife;
			
			System.out.println("New Projectile - Power: " + power + " Life: " + projectileLife);
			
			projectiles.add(p);

			// Reset weapon cooldown.
			timer = cooldown;
		}
	}
	
	void render(OrthographicCamera camera)
	{
		shape.setProjectionMatrix(camera.combined);

		// Draw weapon.
		shape.begin(ShapeType.Line);
		shape.setColor(1, 1, 1, 1);
		Vector3 a = new Vector3(ship.position);
		//a.add(target.x - 1280.0f * 0.5f, -target.y + 720.0f * 0.5f, 0.0f);
		a.add(targetDirection.x * 100.0f, targetDirection.y * 100.0f, 0.0f);
		shape.line(ship.position, a);
		shape.end();
		
		// Draw projectiles.
		shape.begin(ShapeType.Filled);
		shape.setColor(1, 0, 0, 1);
		for (int i = 0; i < projectiles.size; i++)
		{
			Projectile p = (Projectile)projectiles.get(i);
			shape.circle(p.position.x, p.position.y, 3.0f);
		}
		shape.end();
	}
	
	public byte[] getInputArray()
	{
		return inputArray;
	}
	
	public boolean getNewInput()
	{
		return newInput;
	}
	
	public byte[] floatToBytes(float value)
	{
		int bits = Float.floatToIntBits(value);
		byte[] bytes = new byte[4];
		bytes[0] = (byte)(bits & 0xff);
		bytes[1] = (byte)((bits >> 8) & 0xff);
		bytes[2] = (byte)((bits >> 16) & 0xff);
		bytes[3] = (byte)((bits >> 24) & 0xff);
		
		return bytes;
	}
}
