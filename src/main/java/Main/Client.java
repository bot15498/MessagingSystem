package Main;

import Models.User;
import Threads.ClientReceiveThread;
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
		Util.println("Starting Client...");

		// Get nickname for server purposes
		Scanner scan = new Scanner(System.in);
		Util.println("Enter nickname: ");
		String nickname = scan.nextLine();
		User user = new User(nickname);
		ClientReceiveThread printThread = null;

		// make connection to Main.Server.
		try {
			Socket serverSocket = new Socket(hostname, port);
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

			// Start thread for printing messages incomming from server.
			printThread = new ClientReceiveThread(in, user);
			printThread.start();

			// Tell server user information
			out.println(user.toJSONString());

			Util.println("Successfully connected to server.");
			boolean isRunning = true;
			while (isRunning) {
				String command = scan.nextLine();
				handleCommand(out, user, command);
			}
			printThread.stopListening();
		} catch (IOException e) {
			System.err.println("Connection with Main.Server failed.");
			e.printStackTrace();
		}
	}

	public static void handleCommand(PrintWriter out, User user, String command) {
		String[] splits = command.split(" ");
		switch(splits[0]) {
			case "/disconnect":
			case "/leave":
				out.println(MessageFactory.createUserDisconnectRequestMessage(user).toJSONString());
				break;
			case "/whisper":
			case "/message":
				out.println(MessageFactory.createPrivateMessage(user, splits[1],splits[2]));
				break;
			default:
				out.println(MessageFactory.createGlobalMessage(user, command).toJSONString());
				break;
		}
	}
}
