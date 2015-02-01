package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class Utils 
{
	private static float downScale = 0.01f;
	private static float upScale = 1.0f / downScale;
	private static Random generator = new Random(21);
	private static int uniqueId = 0;
	
	public static Vector2 downScale(Vector2 v)
	{
		return new Vector2(v.scl(downScale));
	}
	
	public static float downScale(float f)
	{
		return f * downScale;
	}
	
	public static Vector2 upScale(Vector2 v)
	{
		return new Vector2(v.scl(upScale));
	}
	
	public static float upScale(float f)
	{
		return f * upScale;
	}
	
	public static float getNextRandom()
	{
		return generator.nextFloat();
	}
	
	public static int getUniqueId()
	{
		return ++uniqueId;
	}
}
