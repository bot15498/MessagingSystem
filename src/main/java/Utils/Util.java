package Utils;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
		json.put(Utils.MessageFields.SEND_TIME, System.currentTimeMillis());
		return json;
	}

	public static JSONObject stringToJson(String s) throws ParseException {
		return (JSONObject) new JSONParser().parse(s);
	}

	public static void printlnToChatArea(TextArea area, String s) {
		String old = area.getText();
		area.appendText(s + "\n");
	}

	public static void printlnMessageToChat(TextFlow flow, String bold, String msg) {
		Text sendText = new Text(bold);
		sendText.setStyle("-fx-fill: #000000;-fx-font-weight:bold;-fx-font-size:16px;-fx-font-family: 'Helvetica';");
		Text msgText = new Text(msg);
		msgText.setStyle("-fx-font-size:16px;-fx-fill: #000000;-fx-font-family: 'Helvetica';");
		Platform.runLater(new Runnable() {
			public void run() {
				flow.getChildren().add(new Text(System.lineSeparator()));
				if(msg.equals("")) {
					flow.getChildren().addAll(sendText);
				} else {
					flow.getChildren().addAll(sendText, msgText);
				}
				Scene scene = flow.getScene();
				ScrollPane pane = (ScrollPane) scene.lookup("#textScrollPane");
				pane.setVvalue(1.5);
			}
		});
	}
}
