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
	
	private final Object outboxLock = new Object();
	private final Object inboxLock = new Object();
	
	private int count = 0;
	
	public ConnectionHandler(Socket socket)
	{
		clientSocket = socket;
		
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
		}
		
		System.out.println("Client disconnected " + clientSocket.getRemoteAddress());
	}
	
	private void sendPacket(Packet p)
	{
		try 
		{
			clientSocket.getOutputStream().write(p.getData());
			clientSocket.getOutputStream().flush();
			
			count++;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void receivePacket()
	{
		try 
		{
			if (clientSocket.getInputStream().available() > 0)
			{
				BufferedReader buffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

				synchronized (inboxLock) 
				{
					// Read data from client.
					String d = buffer.readLine();
					packetInbox.add(new Packet(d.getBytes()));
				}
			}
		} 
		catch (IOException e) 
		{
			// Catch if player leaves the game?
			System.err.println("server: error reading buffer");
		}
	}
	
	public int getOutboxSize()
	{
		return this.packetOutbox.size();
	}
	
	public int getInboxSize()
	{
		return this.packetInbox.size();
	}
}
