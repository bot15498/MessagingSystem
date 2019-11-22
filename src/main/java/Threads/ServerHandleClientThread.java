package Threads;

import Main.Server;
import Models.User;
import Utils.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandleClientThread extends Thread {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private boolean isConnected;
	private User currUser = null;

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
				Server.getInstance().addThread(currUser, this);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			System.err.println("Failed to start connection with user.");
		}
		// Main loop for user input.
		if (currUser != null) {
			String inputLine;
			try {
				while (isConnected && (inputLine = in.readLine()) != null) {
					Util.println(inputLine);
					JSONObject json = Util.stringToJson(inputLine);
					handleIncommingMessage(json);
					yield();
				}
			} catch (IOException e) {
				// This gets called if client forcefully disconnects.
				Util.println("User " + currUser.getNickname() + " disconnected.");
				isConnected = false;
				Server.getInstance().removeThread(currUser);
				currUser = null;
			} catch (ParseException e) {
				// TODO Figure out what happens if a client sends garbage.
				// right now just continue listening.
				Util.println("Received unintelligible message from " + currUser.getNickname() + ".");
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
				if (currUser.getNickname().equals(json.get(GlobalMessageFields.SENDER))) {
					JSONObject j = Util.updateTimestamp(json);
					Server.getInstance().broadcastMessage(j);
				}
				break;
			case MessageTypes.PRIVATE_CHAT_MSG:
				// send to only that one person
				if (currUser.getNickname().equals(json.get(PrivateMessageFields.SENDER))) {
					JSONObject j = Util.updateTimestamp(json);
					Server.getInstance().sendPrivateMessage(j);
				}
				break;
			case MessageTypes.SERVER_MSG:
				break;
			case MessageTypes.USER_CONNECT:
				String connectType = (String) json.get(UserConnectMessageFields.CONNECT_TYPE);
				switch(connectType){
					case UserConnectMessageFields.ConnectTypes.INITIAL_CONNECT:
						break;
					case UserConnectMessageFields.ConnectTypes.DISCONNECT:
						isConnected = false;
						Server.getInstance().removeThread(currUser);
						Util.println("User " + currUser.getNickname() + " disconnected.");
						currUser = null;
						break;
				}
				break;
		}
	}
}
