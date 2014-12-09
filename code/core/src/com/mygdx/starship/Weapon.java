package com.mygdx.starship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Weapon// implements InputProcessor 
{
	private ClientPlayer ship;
	private float timer;
	private Vector2 target;
	private Vector2 targetDirection;
	private ShapeRenderer shape;
	private float cooldown; //ms
	
	//private Array<Projectile> projectiles;
	
	private float power;
	private float projectileLife;
	
	//private byte[] inputArray = null;
	//private boolean newInput = false;
	
	//private int inputArrayIdx = 0;
	
	private Vector2 worldPosition;
	private int mouseButton = -1;
	
	public Weapon(ClientPlayer s)
	{
		ship = s;
		target = new Vector2();
		targetDirection = new Vector2();
		shape = new ShapeRenderer();
		cooldown = 1.0f;
		timer = 0.0f;
		power = 1000.0f;
		projectileLife = 2.0f;
		
		//projectiles = new Array<Projectile>();
		
		//inputArray = new byte[20];
		
		worldPosition = new Vector2();
	}
	
	void update(float dt)
	{
		// Reduce cooldown timer.
		timer = timer - dt;
		
		//updateInput(dt);
		//updateProjectiles(dt);
	}
	
	/*
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
	*/
	
	void updateInput(float dt)
	{
		
	}
	
	/*
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
	*/
	
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
		/*
		for (int i = 0; i < projectiles.size; i++)
		{
			Projectile p = (Projectile)projectiles.get(i);
			shape.circle(p.position.x, p.position.y, 3.0f);
		}
		*/
		shape.end();
	}
	
	/*
	public byte[] getInputArray()
	{
		return inputArray;
	}
	*/
	
	/*
	public int getInputArraySize()
	{
		return inputArrayIdx;
	}
	*/
	
	public Vector2 getWorldPosition()
	{
		return worldPosition;
	}
	
	public int getMouseButton() {
		return mouseButton;
	}

	public void setMouseButton(int mouseButton) {
		this.mouseButton = mouseButton;
	}

	/*
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) 
	{
		target.set(x, y);
		worldPosition.set(target.x - 1280.0f * 0.5f, -target.y + 720.0f * 0.5f);
		setMouseButton(button);
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) 
	{
		target.set(x, y);
		worldPosition.set(target.x - 1280.0f * 0.5f, -target.y + 720.0f * 0.5f);
		setMouseButton(-1);
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) 
	{
		target.set(x, y);
		worldPosition.set(target.x - 1280.0f * 0.5f, -target.y + 720.0f * 0.5f);
		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) 
	{
		target.set(x, y);
		worldPosition.set(target.x - 1280.0f * 0.5f, -target.y + 720.0f * 0.5f);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	*/

	public Vector2 getTarget() {
		return target;
	}

	public void setTarget(Vector2 target) {
		this.target = target;
	}

	public Vector2 getTargetDirection() {
		return targetDirection;
	}

	public void setTargetDirection(Vector2 targetDirection) {
		this.targetDirection = targetDirection;
	}
	
	public void setTarget(float x, float y)
	{
		this.target.set(x, y);
	}

	public void setWorldPosition(Vector2 worldPosition) {
		this.worldPosition = worldPosition;
	}
	
	public void setWorldPosition(float x, float y)
	{
		this.worldPosition.set(x, y);
	}
}
