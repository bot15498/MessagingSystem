package ClientUI;

import Models.User;
import Threads.ClientReceiveThread;
import Utils.MessageFactory;
import Utils.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class ClientController {
	@FXML
	protected TextArea chatDisplayArea;
	@FXML
	protected Label nicknameLabel;
	@FXML
	protected TextField inputField;
	@FXML
	protected Button sendButton;
	@FXML
	protected ListView<String> userList;

	private int port;
	private String hostname;
	private boolean isRunning = true;
	private Socket serverSocket;
	private ClientReceiveThread printThread = null;
	private User user;
	private PrintWriter out;
	private BufferedReader in;
	private ArrayList<String> users;

	public ClientController(int port, String hostname) {
		this.port = port;
		this.hostname = hostname;
	}

	@FXML
	public void initialize() {
		askNickname();
		nicknameLabel.setText(user.getNickname());
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
		Scanner scan = new Scanner(System.in);
		Util.println("Enter nickname: ");
		String nickname = scan.nextLine();
		user = new User(nickname);
		users = new ArrayList<String>();
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
					if (users.contains(recipient)) {
						String pm = String.join(" ", Arrays.copyOfRange(splits, 2, splits.length));
						out.println(MessageFactory.createPrivateMessage(user, recipient, pm));
					}
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
}
