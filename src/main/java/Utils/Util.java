package Utils;

public class Util {
	/**
	 * Thread safe println to deal with all the threads that may or may not be running around.
	 * @param s
	 */
	public static void println(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}
}
