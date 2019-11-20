package Models;

import Utils.MessageFactory;
import org.json.simple.JSONObject;

public class User {
	private String nickname;

	public User(String nickname) {
		this.nickname = nickname;
	}

	public User(JSONObject json) {
		nickname = (String) json.get("nickname");
	}

	public String toJSONString() {
		return MessageFactory.createUserConnectRequestMessage(nickname).toJSONString();
	}

	public String getNickname() {
		return nickname;
	}
}
