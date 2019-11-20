package Utils;

import Models.User;
import org.json.simple.JSONObject;

public class MessageFactory {
	public static JSONObject createUserConnectRequestMessage(String nickname) {
		JSONObject s = new JSONObject();
		s.put(UserConnectMessageFields.TYPE, MessageTypes.USER_CONNECT);
		s.put(UserConnectMessageFields.USER_NICKNAME, nickname);
		s.put(UserConnectMessageFields.SEND_TIME, System.currentTimeMillis());
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

	public static JSONObject createPrivateMessage(User sender, User recipient, String msg) {
		JSONObject s = new JSONObject();
		s.put(PrivateMessageFields.TYPE, MessageTypes.PRIVATE_CHAT_MSG);
		s.put(PrivateMessageFields.SENDER, sender.getNickname());
		s.put(PrivateMessageFields.RECIPIENT, recipient.getNickname());
		s.put(PrivateMessageFields.TEXT, msg);
		s.put(PrivateMessageFields.SEND_TIME, System.currentTimeMillis());
		return s;
	}
}
