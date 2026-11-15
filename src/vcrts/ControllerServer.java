package vcrts;

import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is the server socket of the VC Controller. It waits for task client and vehicle
 * owner windows to connect, gives each connection to its own ControllerRunnable thread, and
 * keeps track of the connected windows so the controller can reply to them.
 */
public class ControllerServer implements Runnable {

	private static ServerSocket serverSocket;
	private static ConcurrentHashMap<String,ControllerRunnable> clients = new ConcurrentHashMap<>();

	public void run()
	{
		System.out.println("Server side--");
		try
		{
			serverSocket = new ServerSocket(ClientConnection.PORT);
			while (true)
			{
				Socket clientSocket = serverSocket.accept();
				ControllerRunnable r = new ControllerRunnable(clientSocket);
				Thread t = new Thread(r);
				t.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("VCController has disconnected");
		}
	}

	public static void addClient(String hostName, ControllerRunnable c) {
		clients.put(hostName, c);
	}

	public static void removeClient(String hostName) {
		clients.remove(hostName);
	}

	public static ControllerRunnable getClient(String hostName) {
		return clients.get(hostName);
	}
}