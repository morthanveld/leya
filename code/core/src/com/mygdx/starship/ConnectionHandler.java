package com.mygdx.starship;

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
					//System.out.println("client: outbox size " + packetOutbox.size());
				}
			}

			// Receive packets.
			receivePacket();
			
			//System.out.println("client handler run");
		}
		
		System.out.println("Client disconnected " + clientSocket.getRemoteAddress());
	}
	
	private void sendPacket(Packet p)
	{
		try 
		{
			/*
			byte[] k = p.getData();
			for (byte j : k)
			{
				System.out.print(j + " ");
			}
			System.out.println();
			*/
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

				int type = buffer.read();
								
				if (type == Packet.POSITION)
				{
					byte[] d = new byte[10];
					d[0] = Packet.POSITION;
					
					for (int i = 1; i < 10; i++)
					{
						int b = buffer.read();
						d[i] = (byte) b;
					}
					
					System.out.println("client: msg length " + d.length);
					
					synchronized (inboxLock)
					{
						packetInbox.add(new Packet(d));
					}
					
					count++;
					System.out.println("client: count " + count);
				}
				
				// Read data from client.
				//String d = buffer.readLine();
				//System.out.println("Inbox: " + packetInbox.size());
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
