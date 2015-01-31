package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class PlayerLobby implements Runnable
{
	StarshipServer starshipServer;
	
	public PlayerLobby(StarshipServer server)
	{
		starshipServer = server;
	}
	
	public void run() 
	{
		ServerSocketHints serverSocketHint = new ServerSocketHints();
		// 0 means no timeout.  Probably not the greatest idea in production!
		serverSocketHint.acceptTimeout = 0;


		// Create the socket server using TCP protocol and listening on 9021
		// Only one app can listen to a port at a time, keep in mind many ports are reserved
		// especially in the lower numbers ( like 21, 80, etc )
		ServerSocket serverSocket = Gdx.net.newServerSocket(Protocol.TCP, 1315, serverSocketHint);

		while(true)
		{
			// Create a socket
			SocketHints s = new SocketHints();
			s.tcpNoDelay = true;
			Socket socket = serverSocket.accept(s);

			System.out.println("Client connected " + socket.getRemoteAddress());

			ConnectionHandler connectionHandler = new ConnectionHandler(socket);
			
			// Register connection as new player.
			starshipServer.registerPlayer(connectionHandler);
			
			new Thread(connectionHandler).start();
		}
	}

}
