package Main;

import Models.User;
import Threads.ServerCommandsThread;
import Threads.ServerHandleClientThread;
import Utils.MessageFactory;
import Utils.Util;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Server {
	private ServerSocket serverSocket = null;
	private HashMap<String, ServerHandleClientThread> connectedThreads = new HashMap<String, ServerHandleClientThread>();
	private HashMap<String, User> connectedUsers = new HashMap<String, User>();

	// Static stuff
	private static final int port = 4444;
	private static Server instance;

	public static void main(String[] args) {
		Server server = Server.getInstance();
		server.handleIncommingRequests();
	}

	private Server() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not start Main.Server socket");
			System.exit(1);
		}
		Util.println("Starting Server...");
	}

	public static Server getInstance() {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	public void handleIncommingRequests() {
		Socket clientSocket = null;
		ServerCommandsThread commandsThread = new ServerCommandsThread();
		commandsThread.start();
		// now listen for incomming requests and create a server thread if a socket is found
		while (true) {
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// start thread for handling sockets.
			ServerHandleClientThread thread = new ServerHandleClientThread(clientSocket);
			thread.start();
		}
	}

	public synchronized Collection<User> getListOfUsers() {
		return connectedUsers.values();
	}

	public synchronized void addThread(User user, ServerHandleClientThread thread) {
		connectedThreads.put(user.getNickname(), thread);
		connectedUsers.put(user.getNickname(), user);
	}

	public synchronized void broadcastGlobalMessage(JSONObject msg) {
		for(ServerHandleClientThread thread : connectedThreads.values()) {
			thread.sendMessageToClient(msg);
		}
	}

	public synchronized void broadcastGlobalMessage(User sender, String msg) {
		JSONObject json = MessageFactory.createGlobalMessage(sender, msg);
		for(ServerHandleClientThread thread : connectedThreads.values()) {
			thread.sendMessageToClient(json);
		}
	}
}
