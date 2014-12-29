package com.mygdx.starship;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class ClientInput implements InputProcessor
{
	private byte[] inputArray = null;
	private int inputArrayIdx = 0;
	
	
	private static final int KEY_A = 0;
	private static final int KEY_W = 1;
	private static final int KEY_S = 2;
	private static final int KEY_D = 3;
	private static final int KEY_Q = 4;
	private static final int KEY_E = 5;
	
	private ClientShip clientShip = null;
	private boolean mouseButtonDown = false;
	
	private int id = 0;
	
	public ClientInput(ClientShip clientShip)
	{
		this.clientShip = clientShip;
		
		inputArray = new byte[6];
		for (int i = 0; i < inputArray.length; i++)
		{
			inputArray[i] = 0;
		}	
	}
	
	public void updateInput()
	{
		if (mouseButtonDown)
		{
			// Create packet every frame mouse button is down.
			createOutputPacket();
		}
	}
	
	public byte[] getInputArray()
	{
		return inputArray;
	}
	
	public int getInputArraySize()
	{
		return inputArrayIdx;
	}
	
	public boolean isMouseButtonDown()
	{
		return this.mouseButtonDown;
	}
	
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
		Weapon w = clientShip.getWeapon();
		Vector2 pos = w.getWorldPosition();
		pos.add(clientShip.getPosition());
		
		StringBuffer a = new StringBuffer();
		a.append(this.id);		
		a.append(";");
		a.append(Packet.IO_MOUSE);
		
		a.append(";");
		a.append(pos.x);
		a.append(";");
		a.append(pos.y);
		a.append(";");
		a.append(w.getMouseButton());
		a.append("\n");
		
		//System.out.println("mouse " + a.toString());

		clientShip.getConnectionHandler().addPacket(new Packet(a.toString().getBytes()));
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
		Weapon weapon = clientShip.getWeapon();
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
		Weapon weapon = clientShip.getWeapon();
		weapon.setTarget(x, y);
		weapon.setWorldPosition(x - 1280.0f * 0.5f, -y + 720.0f * 0.5f);
		weapon.setMouseButton(-1);
		createOutputPacket();
		mouseButtonDown = false;
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) 
	{
		Weapon weapon = clientShip.getWeapon();
		weapon.setTarget(x, y);
		weapon.setWorldPosition(x - 1280.0f * 0.5f, -y + 720.0f * 0.5f);
		createOutputPacket();
		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) 
	{
		Weapon weapon = clientShip.getWeapon();
		weapon.setTarget(x, y);
		weapon.setWorldPosition(x - 1280.0f * 0.5f, -y + 720.0f * 0.5f);
		createOutputPacket();
		return true;
	}

	@Override
	public boolean scrolled(int amount) 
	{
		// TODO Auto-generated method stub
		return false;
	}
}
