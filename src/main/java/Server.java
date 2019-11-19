import Threads.ServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	private static final int port = 4444;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		ArrayList<ServerThread> connectedThreads = new ArrayList<ServerThread>();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not start Server socket");
			System.exit(1);
		}
		System.out.println("Starting Server ...");
		// now listen for incomming requests and create a server thread if a socket is found
		while(true) {
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// start thread for handling sockets.
			ServerThread thread = new ServerThread(clientSocket);
			connectedThreads.add(thread);
			thread.start();
		}
	}
}
