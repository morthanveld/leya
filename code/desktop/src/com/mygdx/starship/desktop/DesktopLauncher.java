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
		config.vSyncEnabled = true;
		config.fullscreen = true;
		new LwjglApplication(new Game(), config);
	}
}
