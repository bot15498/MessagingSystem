package Threads;

import Models.User;
import Utils.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientReceiveThread extends Thread {
	private BufferedReader in;
	private boolean isRunning = true;
	private User currUser;

	public ClientReceiveThread(BufferedReader in, User user) {
		this.in = in;
		currUser = user;
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

	private void handleMessage(JSONObject msg) {
		String key = (String) msg.get(MessageFields.TYPE);
		String rawMsg, nickname, recipient;
		switch (key) {
			case MessageTypes.GLOBAL_CHAT_MSG:
				rawMsg = (String) msg.get(GlobalMessageFields.TEXT);
				nickname = (String) msg.get(GlobalMessageFields.SENDER);
				Util.println(nickname + ": " + rawMsg);
				break;
			case MessageTypes.USER_CONNECT:
				// TODO ???
				break;
			case MessageTypes.PRIVATE_CHAT_MSG:
				rawMsg = (String) msg.get(PrivateMessageFields.TEXT);
				nickname = (String) msg.get(PrivateMessageFields.SENDER);
				recipient = (String) msg.get(PrivateMessageFields.RECIPIENT);
				if (currUser.getNickname().equals(recipient)) {
					Util.println(nickname + " (Whisper): " + rawMsg);
				}
				break;
			case MessageTypes.SERVER_MSG:
				// TODO handle server messages?
				break;
		}
	}

	public void stopListening() {
		isRunning = false;
	}
}
