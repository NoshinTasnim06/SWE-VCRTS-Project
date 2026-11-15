package vcrts;
import java.io.*;
import java.net.*;

public class ControllerRunnable implements Runnable {

	Socket clientSocket;
	String hostName;
	String messageIn;
	String messageOut;
	DataInputStream input;
	DataOutputStream output;

	public ControllerRunnable(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}
	
	public void run()
    {
    	messageIn = "";
    	messageOut = "";
    	// The port is added because every window on this computer has the same host name
    	hostName = clientSocket.getInetAddress().getHostName() + ":" + clientSocket.getPort();
		System.out.println(hostName + " connected!");
		
		try {
			input = new DataInputStream(clientSocket.getInputStream());
			output = new DataOutputStream(clientSocket.getOutputStream());
			ControllerServer.addClient(hostName, this);
			while (!messageIn.equals("Exit"))
			{
				messageIn = input.readUTF(); // list of fields
				if (messageIn.equals("Exit"))
					break;
				ControllerPanel.serverRequest(hostName, messageIn);
				messageOut = ClientConnection.ACKNOWLEDGMENT;
				send(messageOut);
			}
			ControllerServer.removeClient(hostName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (hostName != null)
				ControllerServer.removeClient(hostName);
		}
    }
	
	public void reply(String type, boolean accept, String id)
	{
		try
		{
			messageOut = type + " ";
			if (accept)
				messageOut += id + " has been processed";
			else
				messageOut += id + " has been rejected";
			send(messageOut);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// Writes one message; synchronized because the reply comes from the controller window's
	// thread while the acknowledgment comes from this connection's thread
	private synchronized void send(String message) throws IOException
	{
		output.writeUTF(message);
	}
    
}
