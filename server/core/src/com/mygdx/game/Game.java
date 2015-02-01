package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Game
{
	private int VELOCITY_ITERATIONS = 6;
	private int POSITION_ITERATIONS = 2;
	
	private int currentWave = 0;
	private Array<Wave> waves = null;
	
	static final int STATE_LOBBY = 0x1;
	static final int STATE_LOADING_LEVEL = 0x2;
	static final int STATE_START = 0x3;
	static final int STATE_GAME_RUNNING = 0x4;
	static final int STATE_END = 0x5;
	
	private int state = 0;
	private World world = null;
	private Box2DDebugRenderer debugRenderer = null;
	private OrthographicCamera camera = null;
	
	private StarshipServer server = null;
	private Array<Entity> entities = null;
	private Array<RadiusProximity<Vector2>> proximities;
	private Array<Event> events = null;
	
	private int numPlayers = 0;
	
	private ProjectileManager projectileManager = null;
	
	public Game(StarshipServer server)
	{
		this.world = WorldSingleton.getInstance().getWorld();
		
		this.debugRenderer = new Box2DDebugRenderer();
		this.camera = new OrthographicCamera(Utils.downScale(3280), Utils.downScale(1720));
		
		this.server = server;
		this.entities = new Array<Entity>();
		this.proximities = new Array<RadiusProximity<Vector2>>();
		this.events = new Array<Event>();
		this.projectileManager = new ProjectileManager(this.world);
		
		this.waves = new Array<Wave>();
		
		loadLevel();
		
		this.state = STATE_LOBBY;
		
		// TODO: Only to get game testing started.
		//nextState();
	}
	
	public void loadLevel()
	{
		Rock r = new Rock();
		r.createBody(new Vector2(0.0f, 0.0f));
		this.entities.add(r);
		Rock s = new Rock();
		s.createBody(new Vector2(0.1f, 0.0f));
		this.entities.add(s);
		
		Wave w = new Wave(10);
		w.addSpawnLocation(new SpawnLocation(this, Utils.downScale(new Vector2(200.0f, 200.0f))));
		w.addSpawnLocation(new SpawnLocation(this, Utils.downScale(new Vector2(-300.0f, 300.0f))));
		this.waves.add(w);
		
/*		
		for (int i = 0; i < 10; i++)
		{
			Vector2 p = new Vector2((float)Math.random() * 800.0f, (float)Math.random() * 800.0f);
			createEnemy(Utils.downScale(p));
		}
		*/
	}
	
	public void update(float dt)
	{
		readInput();
		
		switch (this.state)
		{
		case STATE_LOBBY:
		{
			// Check if all players are ready.
			int readyCount = 0;
			for (Entity e : entities)
			{
				if (e instanceof Player)
				{
					((Player) e).update(dt);
					if (((Player) e).isReady())
					{
						readyCount++;
					}
				}
			}
			
			if (this.numPlayers > 0 && readyCount == this.numPlayers)
			{
				this.nextState();
			}
			
			break;
		}
		case STATE_LOADING_LEVEL:
		{
			// Send level information to players. Levels are procedurally created by the server.
			System.out.println("Sending level data in update.");

			// Compile packet.
			StringBuffer a = new StringBuffer();
			a.append(Packet.LEVEL);
			
			for (Entity e : entities)
			{
				if (e instanceof Rock)
				{
					Rock r = (Rock) e;
					a.append(";");
					a.append(r.getId());
					a.append(";");
					a.append("0"); // TYPE
					a.append(";");
					a.append(r.getPosition().x);
					a.append(";");
					a.append(r.getPosition().y);
					a.append(";");
					a.append(r.getDirection());
				}
			}
			
			a.append("\n");
			
			Packet levelData = new Packet(a.toString().getBytes());
			
			for (Entity e : entities)
			{
				if (e instanceof Player)
				{
					((Player) e).update(dt);
					((Player) e).addPacket(levelData);
				}
			}
			
			this.nextState();
			
			break;
		}
		case STATE_START:
		{
			// Start current wave.
			if (this.currentWave < this.waves.size)
			{
				this.waves.get(this.currentWave).start();
			}
			
			this.nextState();
			
			break;
		}
		case STATE_GAME_RUNNING:
		{
			// Step box2d physics.
			this.world.step(dt, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			
			// Update waves.
			/*for (Wave wave : this.waves)
			{
				wave.update(dt);
			}
			*/
			
			// Update entities.
			for (Entity e : entities)
			{
				if (e instanceof Enemy)
				{
					((Enemy) e).updateAi(dt);
				}
				if (e instanceof Player)
				{
					((Player) e).update(dt);
				}
				if (e instanceof Ship)
				{
					Ship s = (Ship) e;
					s.updatePhysics(dt);
					if (s.isScheduledDestruction())
					{
						// Create event.
						Event event = new Event();
						event.createEntityDestroy(s.getId());
						events.add(event);
						
						Gdx.app.debug("game", "destroy ship " + s.getId());
						s.destroy();
						entities.removeValue(e, false);
					}
				}					
			}
			
			// Update projectiles.
			projectileManager.updatePhysics(dt);
			
			if (this.currentWave < this.waves.size)
			{
				Wave w = this.waves.get(this.currentWave);
				w.update(dt);
				
				if (w.isWaveFinished())
				{
					Gdx.app.debug("game", "wave is finished, next wave");
					// Go to next wave.
					this.currentWave++;
				}
			}
			
			break;
		}
		}
	}
	
	public void render()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		debugRenderer.render(this.world, camera.combined);
	}
	
	public void addPlayer(ConnectionHandler connection)
	{
		Player p = new Player(this.server, connection);
		p.createBody(new Vector2(0.0f, 0.0f));
		entities.add(p);
		
		Gdx.app.debug("game", "add player " + p.getId());
		
		for (Entity entity : entities)
		{
			if (entity instanceof Enemy)
			{
				((Enemy) entity).setSteeringBehavior(new Pursue<Vector2>(entity, p));
			}
		}

		this.numPlayers++;
	}
	
	public void removePlayer(Player player)
	{
		Gdx.app.debug("game", "remove player " + player.getId());
		this.entities.removeValue(player, false);
		this.numPlayers--;
	}
	
	public void createEnemy(Vector2 position)
	{
		Enemy e = new Enemy();
		e.createBody(position);
		
		// Wander behavior.
		Wander<Vector2> w = new Wander<Vector2>(e);
		w.setFaceEnabled(false);
		w.setWanderOffset(Utils.downScale(100));
		w.setWanderOrientation(Utils.downScale(10));
		w.setWanderRadius(Utils.downScale(300));
		w.setWanderRate(MathUtils.PI * 100);
				
		// Collision avoidance behavior.
		RadiusProximity<Vector2> rp = new RadiusProximity<Vector2>(e, entities, Utils.downScale(300.0f));
		this.proximities.add(rp);
		CollisionAvoidance<Vector2> collisionAvoidanceSB = new CollisionAvoidance<Vector2>(e, rp);
		
		// Add behaviors.
		PrioritySteering<Vector2> prioritySteeringSB = new PrioritySteering<Vector2>(e, 0.0001f);
		prioritySteeringSB.add(collisionAvoidanceSB);
		prioritySteeringSB.add(w);
		
		e.setSteeringBehavior(prioritySteeringSB);
		
		entities.add(e);
	}
	
	public void createRock(Vector2 position)
	{
		// Create rock.
		Rock rock = new Rock();
		rock.createBody(position);
		this.entities.add(rock);
		
		// Send
	}
	
	public void nextState()
	{
		state++;
		
		switch (this.state)
		{
		case STATE_LOBBY:
		{
			Gdx.app.debug("game", "state lobby");
			break;
		}
		case STATE_START:
		{
			Gdx.app.debug("game", "state start");
			break;
		}
		case STATE_GAME_RUNNING:
		{
			Gdx.app.debug("game", "state running");
			break;
		}
		}
		
		if (state > Game.STATE_END)
		{
			state = Game.STATE_END;
		}
	}

	public int getState()
	{
		return this.state;
	}
	
	public Array<Entity> getEntities()
	{
		return this.entities;
	}
	
	public Array<Event> getEvents()
	{
		return this.events;
	}
	
	public ProjectileManager getProjectileManager()
	{
		return this.projectileManager;
	}
	
	public void readInput()
	{
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			Gdx.app.exit();
		}
	}
}
