import java.util.Scanner;

public class Server {

	public static void main(String[] args) {
		System.out.println("hey");
		Scanner scan = new Scanner(System.in);
		while(true) {
			String in = scan.nextLine();
			if(in.equals("quit")) {
				System.exit(0);
			}
		}
	}

}
