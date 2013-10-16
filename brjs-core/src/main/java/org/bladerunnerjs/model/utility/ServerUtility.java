package org.bladerunnerjs.model.utility;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;


public class ServerUtility
{
	private static final int MIN_PORT = 1024;
	private static final int MAX_PORT = 3000;
	private static final int MAX_RANDOM_PORT = MAX_PORT - MIN_PORT;
	
	private static Random random = new Random();
	
	// this has to generate a 'random' port, otherwise it breaks parallel Gradle builds
	public static int getTestPort()
	{
		int port;
		do 
		{
			port = random.nextInt(MAX_RANDOM_PORT) + MIN_PORT;
		} 
		while (isPortBound(port));
		return port;
	}
	
	public static boolean isPortBound(int port)
	{
		try (ServerSocket socket = new ServerSocket(port))
		{
			socket.getClass(); /* reference socket to prevent the compiler complaining that is isn't referenced */
			return false;
		}
		catch (IOException ex)
		{
			return true;
		}
	}
}
