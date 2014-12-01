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
	
	public ConnectionHandler(Socket socket)
	{
		clientSocket = socket;
		packetOutbox = new ArrayDeque<Packet>();
		packetInbox = new ArrayDeque<Packet>();
	}
	
	public void addPacket(Packet p)
	{
		packetOutbox.add(p);
	}
	
	public Packet getPacket()
	{
		if (packetInbox.size() == 0)
		{
			return null;
		}
		return packetInbox.pop();
	}
	
	public void run() 
	{
		while(true)
		{
			if (!clientSocket.isConnected())
			{
				break;
			}
			
			// Check if outbox is not empty.
			if (!packetOutbox.isEmpty())
			{
				// Pop and send packet.
				sendPacket((Packet) packetOutbox.pop());
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

				// Read data from client.
				String d = buffer.readLine();
				packetInbox.add(new Packet(d.getBytes()));
				
				//System.out.println("Inbox: " + packetInbox.size());
			}
		} 
		catch (IOException e) 
		{
			// Catch if player leaves the game?
			System.err.println("server: error reading buffer");
		}
	}
}
