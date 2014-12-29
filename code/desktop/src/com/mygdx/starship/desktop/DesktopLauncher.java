package com.mygdx.starship.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.starship.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Starship";
		config.width = 1280;
		config.height = 720;
		config.fullscreen = false;
		config.forceExit = true;  
		config.vSyncEnabled = false;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		new LwjglApplication(new Game(), config);
	}
}
