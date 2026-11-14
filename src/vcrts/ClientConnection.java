/* Project:  VCRTS
 * Class:    ClientConnection.java
 * Author:   Saffah Azeem
 * Date:     November 14, 2026
 * This class is the client side of the client-server model. A task owner's or
 * vehicle owner's window uses it to send requests to the VC Controller (the
 * server) and to receive the controller's acknowledgment and decision.
*/

package vcrts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class ClientConnection {

	// Where the VC Controller listens. The server must use the same port.
	public static final String HOST = "localhost";
	public static final int PORT = 5000;

	// The reply the server sends as soon as it receives a request
	public static final String ACKNOWLEDGMENT = "Request received";

	// Message types, sent as the first part of every request
	public static final String JOB_REQUEST = "JOB";
	public static final String VEHICLE_REQUEST = "VEHICLE";

	private static final String EXIT_MESSAGE = "Exit";
	private static final String SEPARATOR = "|";
	private static final int CONNECT_TIMEOUT_MS = 3000;

	private final Consumer<String> onMessage;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;

	// ---------------------------------------------------------------------
	// This constructor stores what to do with each message from the server.
	// The handler always runs on the Swing thread, so it can show popups and
	// update tables safely.
	public ClientConnection(Consumer<String> onMessage) {
		this.onMessage = onMessage;
	}

	// ---------------------------------------------------------------------
	// This method returns true while there is an open connection to the server.
	public synchronized boolean isConnected() {
		return socket != null && !socket.isClosed();
	}

	// ---------------------------------------------------------------------
	// This method opens a connection to the server if one isn't open yet, and
	// starts a background thread that listens for the server's replies. It
	// throws an IOException if the server isn't running.
	public synchronized void connect() throws IOException {
		if (isConnected()) {
			return;
		}

		Socket newSocket = new Socket();
		newSocket.connect(new InetSocketAddress(HOST, PORT), CONNECT_TIMEOUT_MS);
		socket = newSocket;
		input = new DataInputStream(newSocket.getInputStream());
		output = new DataOutputStream(newSocket.getOutputStream());

		// Listening blocks until a message arrives, so it runs on its own thread
		// to keep the window responsive. A daemon thread ends when the app closes.
		DataInputStream listenStream = input;
		Thread listener = new Thread(() -> listen(newSocket, listenStream), "VCRTS-server-listener");
		listener.setDaemon(true);
		listener.start();
	}

	// ---------------------------------------------------------------------
	// This method sends a task owner's new job to the server for approval.
	public void sendJob(String clientId, String jobName, int duration, String deadline,
			int priority) throws IOException {
		send(buildMessage(JOB_REQUEST, clientId, jobName, String.valueOf(duration), deadline,
				String.valueOf(priority)));
	}

	// ---------------------------------------------------------------------
	// This method sends a vehicle owner's new vehicle to the server for approval.
	public void sendVehicle(String ownerId, String make, String model, String year,
			String license, String ramGB, String cpuCores, String storageGB,
			String residencyTime) throws IOException {
		send(buildMessage(VEHICLE_REQUEST, ownerId, make, model, year, license, ramGB,
				cpuCores, storageGB, residencyTime));
	}

	// ---------------------------------------------------------------------
	// This method joins a message type and its fields with "|", for example
	// "JOB|amy|Render|30|2026-11-15 18:00|2". A "|" typed inside a field is
	// replaced with "/" so it can't split the message in the wrong place.
	public static String buildMessage(String type, String... fields) {
		StringBuilder message = new StringBuilder(type);
		for (String field : fields) {
			String value = (field == null) ? "" : field.replace(SEPARATOR, "/");
			message.append(SEPARATOR).append(value);
		}
		return message.toString();
	}

	// ---------------------------------------------------------------------
	// This method connects if needed, then writes one message to the server.
	private synchronized void send(String message) throws IOException {
		connect();
		try {
			output.writeUTF(message);
			output.flush();
		} catch (IOException exception) {
			// The server went away since the last request: drop the dead connection
			closeConnection(socket);
			throw exception;
		}
	}

	// ---------------------------------------------------------------------
	// This method runs on the background thread. It waits for each message
	// from the server and hands it to the Swing thread. It ends when the
	// connection closes, after logging out or when the server stops.
	private void listen(Socket listenSocket, DataInputStream listenStream) {
		try {
			while (true) {
				String message = listenStream.readUTF();
				SwingUtilities.invokeLater(() -> onMessage.accept(message));
			}
		} catch (IOException exception) {
			// The connection closed; nothing more to read
		} finally {
			closeConnection(listenSocket);
		}
	}

	// ---------------------------------------------------------------------
	// This method tells the server this client is leaving, then closes the
	// connection. It is called when the user logs out.
	public synchronized void disconnect() {
		if (!isConnected()) {
			return;
		}
		try {
			output.writeUTF(EXIT_MESSAGE);
			output.flush();
		} catch (IOException exception) {
			// The server is already gone; just close below
		}
		closeConnection(socket);
	}

	// ---------------------------------------------------------------------
	// This method closes the given socket and, if it is the current one,
	// clears it so the next request opens a fresh connection.
	private synchronized void closeConnection(Socket oldSocket) {
		try {
			if (oldSocket != null) {
				oldSocket.close();
			}
		} catch (IOException exception) {
			// Already closed
		}
		if (socket == oldSocket) {
			socket = null;
			input = null;
			output = null;
		}
	}
}
