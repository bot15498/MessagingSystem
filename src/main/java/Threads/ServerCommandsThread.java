package Threads;

import Main.Server;
import Models.User;
import Utils.Util;

import java.util.Scanner;

public class ServerCommandsThread extends Thread {
	Scanner scan = null;

	public ServerCommandsThread() {
		scan = new Scanner(System.in);
	}

	public void run() {
		// Do a check for input on the server command line
		while (true) {
			String command = scan.nextLine();
			switch (command) {
				case "/u":
				case "/user":
				case "/users":
					Util.println("Connected Users: ");
					for(User u : Server.getInstance().getListOfUsers()) {
						Util.println("\t" + u.getNickname());
					}
					if(Server.getInstance().getListOfUsers().size() == 0) {
						Util.println("No users connected.");
					}
					break;
				default:
					printHelp();
					break;
			}
		}
	}

	private void printHelp() {
		Util.println("Commands:");
		Util.println("\t/users\t\tPrint list of all connected users.");
		Util.println("\t/help\t\tPrint this help command.");
	}
}
