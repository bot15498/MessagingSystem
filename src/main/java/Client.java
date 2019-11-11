import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final int port = 4444;

	public static void main(String[] args) {
		try {
			Socket serverSocket = new Socket("DESKTOP-IM3S6B7", port);
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			Scanner scan = new Scanner(System.in);

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
