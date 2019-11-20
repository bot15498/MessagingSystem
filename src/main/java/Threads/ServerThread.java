package Threads;

import Models.User;
import Utils.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {
	private Socket socket;
	PrintWriter out;
	BufferedReader in;
	boolean isConnected;

	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			isConnected = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		out.println("Hey");
		User currUser = null;
		try {
			String user = in.readLine();
			JSONObject json = (JSONObject) new JSONParser().parse(user);
			currUser = new User(json);
			System.out.println();
			Util.println("User " + currUser.getNickname() + " connected.");
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			System.err.println("Failed to start connection with user.");
		}
		if (currUser != null) {
			String inputLine;
			try {
				while ((inputLine = in.readLine()) != null) {
					Util.println(inputLine);
					if (inputLine.equals("Hey Hey Start Dash!")) {
						out.println("Nice.");
						break;
					} else {
						out.println("Please laugh.");
					}
					yield();
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Connection error with user: " + currUser.getNickname());
			}
		}
	}
}
