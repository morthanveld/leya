package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class StarshipServer extends ApplicationAdapter 
{
	private Array<Player> players;
	private ProjectileManager projectileManager;
	
	float ioTimer = 0.0f;
	
	private World world = null;
	
	private int VELOCITY_ITERATIONS = 6;
	private int POSITION_ITERATIONS = 2;
	
	Box2DDebugRenderer debugRenderer = null;
	private OrthographicCamera camera;
	
	private Player agent; 
	private Player target;
	
	public void create () 
	{
		//System.out.close();
		Box2D.init();
		world = new World(new Vector2(0.0f, 0.0f), true);
		
		camera = new OrthographicCamera(1280, 720);
		debugRenderer = new Box2DDebugRenderer();
		
		players = new Array<Player>();
		projectileManager = new ProjectileManager(world);
				
		createAiTest();
		
		// Start listening on incoming clients.
		listen();
	}
	
	public void createAiTest()
	{		
		for (int i = 0; i < 1; i++)
		{
			Player p = new Player(this, null);
			p.setPosition(new Vector2((float)Math.random() * 400.0f, (float)Math.random() * 400.0f));
			p.setupPhysics();
			players.add(p);
		}
	}
	
	public World getWorld()
	{
		return this.world;
	}
	
	public void render()
	{
		float dt = 1.0f / 60.0f;
		//float dt = Gdx.graphics.getDeltaTime();
		
		updateInput();
		updatePhysics(dt);
		
		ioTimer += dt;
		if (ioTimer > (1.0 / 60.0f))
		{
			updateOutput();
			ioTimer = 0.0f;
		}
		
		if (Gdx.graphics.getFrameId() % 60 == 0)
		{
			System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond() + " " + Gdx.graphics.getDeltaTime());
		}
		
		try 
		{
			Thread.sleep((long)(1000/60-Gdx.graphics.getDeltaTime()));
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		debugRenderer.render(world, camera.combined);
	}
	
	/*
	 * Game Update
	 * Physics and AI.
	 */
	public void updatePhysics(float dt)
	{
		world.step(dt, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		
		for (Player p : players)
		{
			// Update player ship.
			p.updatePhysics(dt);
			
			// Update AI.
			p.updateAi(dt);
		}
		
		// Update projectiles.
		projectileManager.updatePhysics(dt);
	}
	
	public void updateOutput()
	{
		StringBuffer a = new StringBuffer();
		a.append(Packet.POSITION);
		
		for (Player p : players)
		{
			if (p.getId() != 0)
			{
				// Compile data to send.
				a.append(";");
				a.append(p.getId());
				a.append(";");
				a.append(p.getPosition().x);
				a.append(";");
				a.append(p.getPosition().y);
				a.append(";");
				a.append(p.getDirection());
			}
		}
		
		a.append("\n");
		Packet positionPacket = new Packet(a.toString().getBytes());
		
		String projectileData = projectileManager.getProjectileData();
		Packet projectilePacket = new Packet(projectileData.getBytes());

		// Add position packet to players connection.
		for (Player p : players)
		{
			p.addPacket(positionPacket);
			
			//System.out.println("dirty projs: " + projectileManager.getDirtySize());
			
			if (projectileManager.getDataSize() > 0)
			{
				p.addPacket(projectilePacket);
			}
		}
	}
	
	public void updateInput()
	{
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			Gdx.app.exit();
		}
	}
	
	public void listen()
	{
		Runnable PlayerLobby = new PlayerLobby(this);
		new Thread(PlayerLobby).start();
	}
	
	public void registerPlayer(ConnectionHandler connection)
	{
		Player p = new Player(this, connection);
		players.add(p);
		
		for (Player a : players)
		{
			a.setSteeringBehavior(new Pursue<Vector2>(a, p));
		}
	}
	
	public void unregisterPlayer(Player p)
	{
		System.out.println("server: unregister player " + p.getId());
		players.removeValue(p, false);
	}

	public ProjectileManager getProjectileManager() {
		return projectileManager;
	}
}
