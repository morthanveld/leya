package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Utils;

public class StarshipServer extends ApplicationAdapter 
{
	float ioTimer = 0.0f;
		
	static final short CATEGORY_PLAYER = 0x0001;
	static final short CATEGORY_ENEMY =  0x0002;
	static final short CATEGORY_BULLET = 0x0004;
	static final short CATEGORY_WORLD =  0x0008;
	
	static final short MASK_PLAYER = CATEGORY_ENEMY | CATEGORY_WORLD;
	static final short MASK_ENEMY = CATEGORY_ENEMY | CATEGORY_PLAYER | CATEGORY_WORLD | CATEGORY_BULLET;
	static final short MASK_BULLET = CATEGORY_PLAYER | CATEGORY_ENEMY | CATEGORY_WORLD;
	static final short MASK_WORLD = -1;
	
	private Game game = null;
	
	public void create () 
	{
		Box2D.init();
		
		game = new Game(this);
		
		Gdx.app.setLogLevel(3);
			
		ContactListener cl = new ContactListener(this);
		
		// Start listening on incoming clients.
		listen();
	}
		
	public void render()
	{
		float dt = 1.0f / 60.0f;
		
		this.game.update(dt);
		
		ioTimer += dt;
		if (ioTimer > (1.0f / 60.0f))
		{
			updateOutput();
			ioTimer = 0.0f;
		}
		
		if (Gdx.graphics.getFrameId() % 60 == 0)
		{
			System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond() + " " + Gdx.graphics.getDeltaTime());
		}
				
		this.game.render();
	}
		
	public void updateOutput()
	{
		StringBuffer a = new StringBuffer();
		a.append(Packet.POSITION);
		
		Array<Entity> entities = this.game.getEntities();
		Array<Event> events = this.game.getEvents();
		ProjectileManager projectileManager = this.game.getProjectileManager();
		
		for (Entity e : entities)
		{
			if (e instanceof Ship)
			{
				Ship s = (Ship) e;

				if (s.getId() != 0)
				{
					// Compile data to send.
					a.append(";");
					a.append(s.getId());
					a.append(";");
					a.append(s.getType());
					a.append(";");
					a.append(s.getPosition().x);
					a.append(";");
					a.append(s.getPosition().y);
					a.append(";");
					a.append(s.getDirection());
				}
			}
			
			if (e instanceof Rock)
			{
				Rock r = (Rock) e;
				a.append(";");
				a.append(r.getId());
				a.append(";");
				a.append(r.getType());
				a.append(";");
				a.append(r.getPosition().x);
				a.append(";");
				a.append(r.getPosition().y);
				a.append(";");
				a.append(r.getDirection());
			}
		}
		
		a.append("\n");
		Packet positionPacket = new Packet(a.toString().getBytes());
		
		String projectileData = projectileManager.getProjectileData();
		Packet projectilePacket = new Packet(projectileData.getBytes());

		// Add position packet to players connection.
		for (Entity e : entities)
		{
			if (e instanceof Player)
			{
				Player p = (Player) e;
			
				p.addPacket(positionPacket);
			
				//System.out.println("dirty projs: " + projectileManager.getDirtySize());

				if (projectileManager.getDataSize() > 0)
				{
					p.addPacket(projectilePacket);
				}
				
				if (events.size > 0)
				{
					StringBuffer eventBuffer = new StringBuffer();
					eventBuffer.append(Packet.EVENT);
					
					while (events.size > 0)
					{
						Event event = events.pop();
						
						switch (event.getType())
						{
						case Event.EVENT_ENTITY_DESTROY:
						{						
							eventBuffer.append(";");
							eventBuffer.append(event.getType());
							eventBuffer.append(";");
							eventBuffer.append(event.getEntityId());
							break;
						}
						}
					}
					
					eventBuffer.append("\n");
					
					p.addPacket(new Packet(eventBuffer.toString().getBytes()));
				}
			}
		}
	}
		
	public void listen()
	{
		Runnable PlayerLobby = new PlayerLobby(this);
		new Thread(PlayerLobby).start();
	}
	
	public void registerPlayer(ConnectionHandler connection)
	{
		this.game.addPlayer(connection);
	}
	
	public void unregisterPlayer(Player player)
	{
		this.game.removePlayer(player);
	}
	
	public Game getGame()
	{
		return this.game;
	}
}
