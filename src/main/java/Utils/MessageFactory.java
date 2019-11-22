package Utils;

import Models.User;
import org.json.simple.JSONObject;

public class MessageFactory {
	public static JSONObject createUserInitConnectRequestMessage(String nickname) {
		JSONObject s = new JSONObject();
		s.put(UserConnectMessageFields.TYPE, MessageTypes.USER_CONNECT);
		s.put(UserConnectMessageFields.USER_NICKNAME, nickname);
		s.put(UserConnectMessageFields.SEND_TIME, System.currentTimeMillis());
		s.put(UserConnectMessageFields.CONNECT_TYPE, UserConnectMessageFields.ConnectTypes.INITIAL_CONNECT);
		return s;
	}

	public static JSONObject createUserDisconnectRequestMessage(User user) {
		JSONObject s = new JSONObject();
		s.put(UserConnectMessageFields.TYPE, MessageTypes.USER_CONNECT);
		s.put(UserConnectMessageFields.USER_NICKNAME, user.getNickname());
		s.put(UserConnectMessageFields.SEND_TIME, System.currentTimeMillis());
		s.put(UserConnectMessageFields.CONNECT_TYPE, UserConnectMessageFields.ConnectTypes.DISCONNECT);
		return s;
	}

	public static JSONObject createGlobalMessage(User sender, String msg) {
		JSONObject s = new JSONObject();
		s.put(GlobalMessageFields.TYPE, MessageTypes.GLOBAL_CHAT_MSG);
		s.put(GlobalMessageFields.SENDER, sender.getNickname());
		s.put(GlobalMessageFields.TEXT, msg);
		s.put(GlobalMessageFields.SEND_TIME, System.currentTimeMillis());
		return s;
	}

	public static JSONObject createPrivateMessage(User sender, String recipient, String msg) {
		JSONObject s = new JSONObject();
		s.put(PrivateMessageFields.TYPE, MessageTypes.PRIVATE_CHAT_MSG);
		s.put(PrivateMessageFields.SENDER, sender.getNickname());
		s.put(PrivateMessageFields.RECIPIENT, recipient);
		s.put(PrivateMessageFields.TEXT, msg);
		s.put(PrivateMessageFields.SEND_TIME, System.currentTimeMillis());
		return s;
	}

	public static JSONObject createShutdownMessage() {
		JSONObject s = new JSONObject();
		s.put(ServerMessageFields.TYPE, MessageTypes.SERVER_MSG);
		s.put(ServerMessageFields.NOTIFICATION, ServerMessageFields.NotificationTypes.SERVER_SHUTDOWN);
		s.put(ServerMessageFields.TEXT, "Server is shutting down.");
		return s;
	}
}
