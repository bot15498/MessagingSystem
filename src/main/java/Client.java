import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final int port = 4444;
	private static final String hostname = "DESKTOP-IM3S6B7";

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
//		System.out.print("");
//		String username = scan.nextLine();


		try {
			Socket serverSocket = new Socket(hostname, port);
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			System.out.println("Starting Client.");

//			JSONObject initUserMsg = new JSONObject();
//			initUserMsg.put("timestamp", System.currentTimeMillis());
//			initUserMsg.put("username", username);
//
//			out.println(initUserMsg);


			String fromServer;
			while ((fromServer = in.readLine()) != null) {
				System.out.println("Server: " + fromServer);
				if (fromServer.equals("Nice."))
					break;
				String userString = scan.nextLine();
				out.println(userString);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
