package Threads;

import Main.Server;
import Models.User;
import Utils.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandleClientThread extends Thread {
	private Socket socket;
	PrintWriter out;
	BufferedReader in;
	boolean isConnected;
	User currUser = null;

	public ServerHandleClientThread(Socket clientSocket) {
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
		// Handle message from client about which user is connecting.
		try {
			String user = in.readLine();
			JSONObject json = Util.stringToJson(user);
			if (json.get(UserConnectMessageFields.TYPE).equals(MessageTypes.USER_CONNECT)) {
				currUser = new User(json);
				Util.println("User " + currUser.getNickname() + " connected.");
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			System.err.println("Failed to start connection with user.");
		}
		if (currUser != null) {
			String inputLine;
			try {
				while ((inputLine = in.readLine()) != null) {
					Util.println(inputLine);
					JSONObject json = Util.stringToJson(inputLine);
					handleIncommingMessage(json);
					yield();
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Connection error with user: " + currUser.getNickname());
			} catch (ParseException e) {
				// TODO Figure out what happens if a client sends garbage.
				// right now just continue listening.
				Util.println("Received uninteligible message from " + currUser.getNickname() + ".");
			}
		}
	}

	public void sendMessageToClient(JSONObject msg) {
		out.println(msg.toJSONString());
	}

	private void handleIncommingMessage(JSONObject json) {
		String type = (String) json.get(MessageFields.TYPE);
		switch (type) {
			case MessageTypes.GLOBAL_CHAT_MSG:
				// just update the timestamp and user (just in case) and rebraodcast.
				if(currUser.getNickname().equals(json.get(GlobalMessageFields.SENDER))) {
					Util.updateTimestamp(json);
					Server.getInstance().broadcastGlobalMessage(json);
				}
				break;
			case MessageTypes.PRIVATE_CHAT_MSG:
				break;
			case MessageTypes.SERVER_MSG:
				break;
			case MessageTypes.USER_CONNECT:
				// Oh god what are you doing client.
				break;
		}
	}
}
