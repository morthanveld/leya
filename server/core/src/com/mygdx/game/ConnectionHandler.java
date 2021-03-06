package com.mygdx.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;

import com.badlogic.gdx.net.Socket;

public class ConnectionHandler implements Runnable 
{
	private final Socket clientSocket;
	private ArrayDeque<Packet> packetOutbox;
	private ArrayDeque<Packet> packetInbox; 
	
	private BufferedReader inputBuffer = null;
	
	private final Object outboxLock = new Object();
	private final Object inboxLock = new Object();
	
	private boolean disconnected = false;
	
	public ConnectionHandler(Socket socket)
	{
		clientSocket = socket;
		inputBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		
		synchronized (outboxLock) 
		{
			packetOutbox = new ArrayDeque<Packet>();
		}
		
		synchronized (inboxLock) 
		{
			packetInbox = new ArrayDeque<Packet>();
		}
	}
	
	public void addPacket(Packet p)
	{
		synchronized (outboxLock) 
		{
			packetOutbox.add(p);
		}
	}
	
	public Packet getPacket()
	{
		synchronized (inboxLock) 
		{
			if (packetInbox.size() == 0)
			{
				return null;
			}
			return packetInbox.pop();
		}
	}
	
	public void run() 
	{
		while(true)
		{
			if (!clientSocket.isConnected())
			{
				break;
			}
			if (disconnected)
			{
				break;
			}
					
			synchronized (outboxLock) 
			{
				// Check if outbox is not empty.
				if (!packetOutbox.isEmpty())
				{
					// Pop and send packet.
					sendPacket((Packet) packetOutbox.pop());
				}
			}
			
			// Receive packets.
			receivePacket();
			
			
			try 
			{
				// Reduce CPU time.
				Thread.sleep(1);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
	
	private void sendPacket(Packet p)
	{
		try 
		{
			clientSocket.getOutputStream().write(p.getData());
			clientSocket.getOutputStream().flush();
		} 
		catch (IOException e) 
		{
			System.err.println("server: client disconnected " + clientSocket.getRemoteAddress());
			disconnected = true;
		}
	}
	
	private void receivePacket()
	{
		try 
		{
			if (clientSocket.getInputStream().available() > 0)
			{
				synchronized (inboxLock) 
				{
					// Read data from client.
					String d = inputBuffer.readLine();
					
					synchronized (inboxLock) 
					{
						packetInbox.add(new Packet(d.getBytes()));
					}
				}
			}
		} 
		catch (IOException e) 
		{
			// Catch if player leaves the game?
			System.err.println("server: error reading buffer");
			disconnected = true;
		}
	}
	
	public int getOutboxSize()
	{
		synchronized (outboxLock)
		{
			return this.packetOutbox.size();
		}
	}
	
	public int getInboxSize()
	{
		synchronized (inboxLock) 
		{
			return this.packetInbox.size();
		}
	}
	
	public boolean isDisconnected()
	{
		return disconnected;
	}
}
