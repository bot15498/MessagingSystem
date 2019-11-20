import Models.User;
import Threads.ClientReceiveThread;
import Threads.PrintThread;
import Utils.MessageFactory;
import Utils.Util;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final int port = 4444;
	private static final String hostname = "DESKTOP-IM3S6B7";

	public static void main(String[] args) {
		// Start thread that prints to console.
//		PrintThread printer = PrintThread.getInstance();
//		printer.addToQueue("Starting Client...");
		Util.println("Starting Client...");


		// Get nickname for server purposes
		Scanner scan = new Scanner(System.in);
		Util.println("Enter nickname: ");
		String nickname = scan.nextLine();
		User user = new User(nickname);
		ClientReceiveThread printThread;

		// make connection to Server.
		try {
			Socket serverSocket = new Socket(hostname, port);
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

			// Start thread for printing messages incomming from server.
			printThread = new ClientReceiveThread(in, user);
			printThread.start();

			// Tell server user information
			out.println(user.toJSONString());
//			fromServer = in.readLine();
//			if(fromServer != null) {
//				out.println(user.toJSONString());
//			}

			boolean isRunning = true;
			while (isRunning) {
				String command = scan.nextLine();
				out.println(MessageFactory.createGlobalMessage(user, command).toJSONString());
			}
			printThread.stopListening();
		} catch (IOException e) {
			System.err.println("Connection with Server failed.");
			e.printStackTrace();
		}
	}
}
