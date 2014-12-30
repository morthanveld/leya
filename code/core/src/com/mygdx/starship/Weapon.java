package com.mygdx.starship;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Weapon 
{
	private ClientShip ship;
	private float timer;
	private Vector2 target;
	private Vector2 targetDirection;
	private ShapeRenderer shape;
	
	private Vector2 worldPosition;
	private int mouseButton = -1;
	
	public Weapon(ClientShip s)
	{
		ship = s;
		target = new Vector2();
		targetDirection = new Vector2();
		shape = new ShapeRenderer();
		timer = 0.0f;
		
		worldPosition = new Vector2();
	}
	
	void update(float dt)
	{
		// Reduce cooldown timer.
		timer = timer - dt;
	}
	
	void updateInput(float dt)
	{
		
	}
		
	void render(OrthographicCamera camera)
	{
		shape.setProjectionMatrix(camera.combined);

		// Draw weapon.
		shape.begin(ShapeType.Line);
		shape.setColor(1, 1, 1, 1);
		Vector2 a = ship.getPosition();
		//a.add(target.x - 1280.0f * 0.5f, -target.y + 720.0f * 0.5f, 0.0f);
		a.add(targetDirection.x * 100.0f, targetDirection.y * 100.0f);
		shape.line(ship.getPosition(), a);
		shape.end();		
	}
		
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
