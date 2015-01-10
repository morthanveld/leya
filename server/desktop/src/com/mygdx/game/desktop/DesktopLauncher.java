package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.mygdx.game.StarshipServer;

public class DesktopLauncher {
	public static void main (String[] arg) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Server";
		config.width = 512;
		config.height = 512;
		config.fullscreen = false;
		config.forceExit = true;  
		config.vSyncEnabled = false;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		new LwjglApplication(new StarshipServer(), config);
	}
}
