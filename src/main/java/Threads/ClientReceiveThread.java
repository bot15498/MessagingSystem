package Threads;

import ClientUI.ClientController;
import Main.Client;
import Models.User;
import Utils.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ClientReceiveThread extends Thread {
	private BufferedReader in;
	private boolean isRunning = true;
	private User currUser;
	private ClientController client;

	public ClientReceiveThread(ClientController client, BufferedReader in, User user) {
		this.in = in;
		currUser = user;
		this.client = client;
	}

	public void run() {
		// Keep checking for new data from server. If you get something, handle it appropriately
		while (isRunning) {
			try {
				if (in.ready()) {
					String rawString = in.readLine();
					JSONObject msg = (JSONObject) new JSONParser().parse(rawString);
					handleMessage(msg);
				}
			} catch (IOException | ParseException e) {
				// maybe force close the client here?
//				isRunning = false;
				e.printStackTrace();
			}
			yield();
		}
	}

	/**
	 * Handle the 4 different message types that can come in from the server.
	 *
	 * @param msg JSONObject that came in as a message.
	 */
	private void handleMessage(JSONObject msg) {
		String key = (String) msg.get(MessageFields.TYPE);
		String rawMsg, nickname, recipient;
		switch (key) {
			case MessageTypes.GLOBAL_CHAT_MSG:
				rawMsg = (String) msg.get(GlobalMessageFields.TEXT);
				nickname = (String) msg.get(GlobalMessageFields.SENDER);
				Util.println(nickname + ": " + rawMsg);
				Util.printlnToChatArea(client.getChatShowText(), nickname + ": " + rawMsg);
				break;
			case MessageTypes.USER_CONNECT:
				// TODO ???
				break;
			case MessageTypes.PRIVATE_CHAT_MSG:
				rawMsg = (String) msg.get(PrivateMessageFields.TEXT);
				nickname = (String) msg.get(PrivateMessageFields.SENDER);
				recipient = (String) msg.get(PrivateMessageFields.RECIPIENT);
				if (currUser.getNickname().equals(recipient) || currUser.getNickname().equals(nickname)) {
					Util.println(nickname + " to " + recipient + ": " + rawMsg);
					Util.printlnToChatArea(client.getChatShowText(), nickname + " to " + recipient + ": " + rawMsg);
				}
				break;
			case MessageTypes.SERVER_MSG:
				handleServerMessages(msg);
				break;
		}
	}

	/**
	 * Shutdown socket thread that listens for text from server.
	 */
	public void stopListening() {
		isRunning = false;
	}

	/**
	 * Handle the different types of Server Messages that the server can send.
	 *
	 * @param json The JSONObject message from server.
	 */
	private void handleServerMessages(JSONObject json) {
		String key = (String) json.get(ServerMessageFields.NOTIFICATION);
		String rawMsg;
		switch (key) {
			case ServerMessageFields.NotificationTypes.SERVER_SHUTDOWN:
				rawMsg = (String) json.get(ServerMessageFields.TEXT);
				Util.println(rawMsg);
				Util.printlnToChatArea(client.getChatShowText(), rawMsg);
				client.stopClient();
				break;
			case ServerMessageFields.NotificationTypes.USER_CONNECTED:
				// print the message which should have new name of person then update users list
			case ServerMessageFields.NotificationTypes.USERS_UPDATE:
				rawMsg = (String) json.get(ServerMessageFields.TEXT);
				Util.println(rawMsg);
				Util.printlnToChatArea(client.getChatShowText(), rawMsg);
				// update users list
				JSONArray ja = (JSONArray) json.get(ServerMessageFields.ALL_USERS);
				ArrayList<String> names = new ArrayList<String>();
				for (int i = 0; i < ja.size(); i++) {
					names.add((String) ja.get(i));
				}
				client.updateUsers(names);
				break;
			case ServerMessageFields.NotificationTypes.WARNING:
				// just display the warning
				rawMsg = (String) json.get(ServerMessageFields.TEXT);
				Util.println("SERVER WARNING: " + rawMsg);
				Util.printlnToChatArea(client.getChatShowText(), "SERVER WARNING: " + rawMsg);
				break;
		}
	}
}
