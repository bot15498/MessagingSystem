package ClientUI;

import Models.User;
import Threads.ClientReceiveThread;
import Utils.MessageFactory;
import Utils.Util;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ClientController {
	@FXML
	protected TextArea chatDisplayArea;
	@FXML
	protected TextFlow chatDisplayFlow;
	@FXML
	protected Label nicknameLabel;
	@FXML
	protected TextField inputField;
	@FXML
	protected Button sendButton;
	@FXML
	protected ListView<String> userList;
	@FXML
	protected ScrollPane textScrollPane;

	private int port;
	private String hostname;
	private boolean isRunning = true;
	private Socket serverSocket;
	private ClientReceiveThread printThread = null;
	private User user;
	private PrintWriter out;
	private BufferedReader in;
	private ArrayList<String> users;

	public ClientController() {
	}

	@FXML
	public void initialize() {
		askNickname();
		nicknameLabel.setText(user.getNickname());
		textScrollPane.setFocusTraversable(false);
		userList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				String clickedName = userList.getSelectionModel().getSelectedItem();
				if (clickedName != null && !clickedName.equals("null") && !inputField.getText().startsWith("/")) {
					String curr = inputField.getText();
					inputField.setText("/whisper " + clickedName + " " + curr);
					inputField.requestFocus();
				}
			}
		});
		startListening();
	}

	@FXML
	public void sendButtonClicked(Event e) {
		// get text in input field and parse it
		String command = inputField.getText();
		handleCommand(out, user, command);
		inputField.clear();
	}

	@FXML
	public void onEnterInText(ActionEvent ae) {
		// get text in input field and parse it
		String command = inputField.getText();
		handleCommand(out, user, command);
		inputField.clear();
	}

	private void askNickname() {
		// Get nickname for server purposes
//		Scanner scan = new Scanner(System.in);
//		Util.println("Enter nickname: ");
//		String nickname = scan.nextLine();
//		user = new User(nickname);
//		users = new ArrayList<String>();

		String nickname = "";
		String host = "";
		int port = -1;
		while (nickname.equals("")) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setTitle("Enter Nickname");
			dialog.setHeaderText("Enter chat nickname:");
			dialog.setContentText("Name:");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				nickname = result.get();
			}
		}
		while (host.equals("")) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setTitle("Enter Host");
			dialog.setHeaderText("Enter Hostname:");
			dialog.setContentText("Hostname:");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				host = result.get();
			}
		}
		while (port == -1) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setTitle("Enter Port");
			dialog.setHeaderText("Enter Port to Connect To:");
			dialog.setContentText("Port:");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				String portStr = result.get();
				try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException e) {
					port = -1;
				}
			}
		}

		user = new User(nickname);
		users = new ArrayList<String>();
		this.port = port;
		this.hostname = host;
	}

	private void startListening() {
		// make connection to Server.
		try {
			serverSocket = new Socket(hostname, port);
			out = new PrintWriter(serverSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

			// Start thread for printing messages incomming from server.
			printThread = new ClientReceiveThread(this, in, user);
			printThread.start();

			// Tell server user information
			out.println(user.toJSONString());
			isRunning = true;
		} catch (IOException e) {
			System.out.println("Connection with Server failed. Closing Program.");
			// create a alert
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setContentText("Connection with Server failed. Closing Program.");
			a.showAndWait();
			System.exit(0);
		}
	}

	private void handleCommand(PrintWriter out, User user, String command) {
		String[] splits = command.split(" ");
		switch (splits[0]) {
			case "/disconnect":
			case "/stop":
			case "/leave":
				out.println(MessageFactory.createUserDisconnectRequestMessage(user).toJSONString());
				// Now we exit
				isRunning = false;
				Stage stage = (Stage) userList.getScene().getWindow();
				stage.close();
				break;
			case "/whisper":
			case "/message":
				if (splits.length > 2) {
					String recipient = splits[1];
					// Check to see of recipient is in server or not.
//					if (users.contains(recipient)) {
//						String pm = String.join(" ", Arrays.copyOfRange(splits, 2, splits.length));
//						out.println(MessageFactory.createPrivateMessage(user, recipient, pm));
//					}
					String pm = String.join(" ", Arrays.copyOfRange(splits, 2, splits.length));
					out.println(MessageFactory.createPrivateMessage(user, recipient, pm));
				}
				break;
			case "/users":
			case "/u":
				Util.println("Connected Users: ");
				for (String nickname : users) {
					Util.println("\t" + nickname);
				}
				break;
			default:
				out.println(MessageFactory.createGlobalMessage(user, command).toJSONString());
				break;
		}
	}


	public void stopClient() {
		if (serverSocket != null && serverSocket.isConnected()) {
			try {
				out.println(MessageFactory.createUserDisconnectRequestMessage(user).toJSONString());
				serverSocket.close();
				Util.println("Socket closed.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	public synchronized void updateUsers(Collection<String> newUsers) {
		users = null;
		users = new ArrayList<>(newUsers);
		Platform.runLater(new Runnable() {
			public void run() {
				userList.getItems().clear();
				userList.getItems().addAll(users);
			}
		});
	}

	public TextArea getChatShowText() {
		return chatDisplayArea;
	}

	public TextFlow getChatFlow() {
		return chatDisplayFlow;
	}
}
