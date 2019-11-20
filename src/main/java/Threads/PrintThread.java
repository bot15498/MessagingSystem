package Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class PrintThread extends Thread {
	private static PrintThread instance;
	// The queue of strings to print.
	private ArrayList<String> stringsToPrint;
	private ArrayList<BufferedReader> inStreams;

	private PrintThread() {
		stringsToPrint = new ArrayList<String>();
		inStreams = new ArrayList<BufferedReader>();
	}

	public static PrintThread getInstance() {
		if (instance == null) {
			instance = new PrintThread();
		}
		return instance;
	}

	public void run() {
		// keep printing from the queue until it is empty
		while (true) {
			if (!stringsToPrint.isEmpty()) {
				String s = stringsToPrint.remove(0);
				System.out.println(s);
			} else {
				// iterate through outstreams and print if it is ready. Order isn't too important here
				try {
					for (BufferedReader in : inStreams) {
						// if a stream has stuff to write, write one line and move on. Only write one line just in case
						if (in.ready()) {
							String s = in.readLine();
							System.out.println(s);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// Then yield after each one.
			yield();
		}
	}

	public boolean addToQueue(String s) {
		return stringsToPrint.add(s);
	}

	public void addOutStream(BufferedReader out) {
		inStreams.add(out);
	}
}
