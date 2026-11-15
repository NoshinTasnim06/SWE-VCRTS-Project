package vcrts;
import java.io.*;
import java.net.*;

public class ControllerRunnable implements Runnable {

	static Socket clientSocket;
	String hostName;
	String messageIn;
	String messageOut;
	static DataInputStream input;
	static DataOutputStream output;

	public ControllerRunnable(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}
	
	public void run()
    {
    	messageIn = "";
    	messageOut = "";
    	hostName = clientSocket.getInetAddress().getHostName();
		System.out.println(hostName + " connected!");
		
		try {
			input = new DataInputStream(clientSocket.getInputStream());
			output = new DataOutputStream(clientSocket.getOutputStream());
			ControllerPanel.addClient(hostName, this);
			while (!messageIn.equals("Exit"))
			{
				messageIn = input.readUTF(); // list of fields
				//add server popup
				ControllerPanel.serverRequest(hostName, messageIn);
				messageOut = "Request received";
				output.writeUTF(messageOut);
			}
			ControllerPanel.removeClient(hostName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (hostName != null)
				ControllerPanel.removeClient(hostName);
		}
    }
	
	public void reply(boolean accept, String id)
	{
		try
		{
			if (hostName.contains("Client"))
				messageOut = "Job ";
			else
				messageOut = "Vehicle ";
			if (accept)
				messageOut += id + " has been processed";
			else
				messageOut += id + " has been rejected";
			output.writeUTF(messageOut);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
    
}
