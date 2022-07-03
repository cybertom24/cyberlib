package cyberLib.io;

import java.util.HexFormat;
import java.util.IllegalFormatException;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * A class full of static methods to handle input from the terminal.
 * It uses the {@code java.util.Scanner} to receive data from the console.
 * The methods have dedicated code to catch exception and handle non formatted text and non desired inputs. 
 * 
 */
public class Input {
	
	private static final Scanner SCANNER = new Scanner(System.in);
	
	/**
	 * Static method for asking the user a {@code String} input with a title being printed before.
	 * @param  title The phrase that it's displayed before it asks the input
	 * @return  The string passed through the console
	 */
	public static String askString(String title) {
		printTitle(title);
		return askString();
	}
	
	/**
	 * Static method for asking the user a {@code String} input.
	 * @return  The string passed through the console
	 */
	public static String askString() {		
		String answerString = null;
		answerString = SCANNER.next();
		return answerString;
	}
	
	/**
	 * Static method for asking the user a {@code int} input.
	 * If the scanner fails to recognize the int a red message stating {@code This is not an int} is displayed.
	 * @param  title The phrase that it's displayed before it asks the input
	 * @return  The int passed through the console
	 */
	public static int askInt(String title) {		
		printTitle(title);
		int x = 0;
		boolean wrong = true;
		while (wrong) {
			try {
				x = SCANNER.nextInt();
				wrong = false;
			} catch (NoSuchElementException e) {
				System.err.println("This is not a number");
				SCANNER.next();
				wrong = true;
			}
		}
		return x;
	}
	
	public static long askHexOrInt(String title) {
		printTitle(title);
		long x = 0;
		boolean wrong = true;
		while (wrong) {
			try {
				String next = SCANNER.next();
				if(next.startsWith("0x")) {
					next = next.substring(2);
					x = HexFormat.fromHexDigitsToLong(next);
				} else
					x = Long.parseLong(next);
				wrong = false;
			} catch (IllegalFormatException e) {
				System.err.println("This is not a number");
				SCANNER.next();
				wrong = true;
			}
		}
		return x;
	}
	
	private static void printTitle(String title) {
		System.out.print(title + ": ");
	}
}
