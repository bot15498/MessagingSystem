import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	private static final int port = 4444;

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			Socket clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			out.println("Hey");
			String input;
			while((input = in.readLine()) != null) {
				if(input.equals("Hey start dash!")) {
					out.println("Nice.");
					break;
				} else {
					out.println("please laugh.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
