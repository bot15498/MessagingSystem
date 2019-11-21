package Models;

import Utils.MessageFactory;
import Utils.UserConnectMessageFields;
import org.json.simple.JSONObject;

public class User {
	private String nickname;

	public User(String nickname) {
		this.nickname = nickname;
	}

	public User(JSONObject json) {
		nickname = (String) json.get(UserConnectMessageFields.USER_NICKNAME);
	}

	public String toJSONString() {
		return MessageFactory.createUserInitConnectRequestMessage(nickname).toJSONString();
	}

	public String getNickname() {
		return nickname;
	}
}
