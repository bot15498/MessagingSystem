package Utils;

import javafx.scene.control.TextArea;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Util {
	/**
	 * Thread safe println to deal with all the threads that may or may not be running around.
	 *
	 * @param s
	 */
	public static void println(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}

	public static JSONObject updateTimestamp(JSONObject json) {
		json.put(MessageFields.SEND_TIME, System.currentTimeMillis());
		return json;
	}

	public static JSONObject stringToJson(String s) throws ParseException {
		return (JSONObject) new JSONParser().parse(s);
	}

	public static void printlnToChatArea(TextArea area, String s) {
		String old = area.getText();
		area.setText(old + s + "\n");
	}
}
