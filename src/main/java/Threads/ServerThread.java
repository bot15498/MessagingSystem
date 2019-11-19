package Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread{
	private Socket socket;
	PrintWriter out;
	BufferedReader in;
	boolean isConnected;

	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			isConnected = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		out.println("Hey");
		String inputLine;
		try {
			while((inputLine = in.readLine()) != null) {
				if(inputLine.equals("Hey Hey Start Dash!")) {
					out.println("Nice.");
					break;
				} else {
					out.println("Please laugh.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
