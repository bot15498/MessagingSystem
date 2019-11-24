package Main;

import ClientUI.ClientController;
import Utils.Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {
	private static final int port = 4444;
	private static final String hostname = "DESKTOP-IM3S6B7";
	private ClientController controller;

	public static void main(String[] args) {
		Application.launch(args);
//		Client client = new Client();
//		client.askNickname();
//		client.startListening();
	}

	public Client() {
		Util.println("Starting Client...");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// scene builder is high quality trash.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/client2.fxml"));
//		controller = new ClientController(port, hostname);
		controller = new ClientController();
		loader.setController(controller);
		Parent root = loader.load();
		primaryStage.setTitle("Aol Massager 2");

		Scene scene = new Scene(root, 1280, 720);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		// Exited JavaFX normally so close server.
		controller.stopClient();
	}
}
