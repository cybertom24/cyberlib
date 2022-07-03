package cyberLib.io;

import java.util.Scanner;

public class Console {

	private static final String PAUSE_MESSAGE = "Press ENTER to continue...";
	private static final Scanner SCANNER = new Scanner(System.in);
	
	public static void pause() {
		System.out.println(PAUSE_MESSAGE);
		SCANNER.useDelimiter("\n");
		SCANNER.next();
	}
}
