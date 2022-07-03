package cyberLib;

import static java.lang.Math.*;

/**
 * A class full of static methods to generate random numbers, get a true or false with user
 * definable probability etc.. 
 */
public class Random {
	
	/**
	 * Get a {@code true} or {@code false} response with the probability passed as the argument
	 * @param  probability A {@code float} value which defines the probability of getting {@code true}
	 * @return  A {@code boolean} that can be {@code true} or {@code false} depending on the {@code probability} passed
	 */
	public static boolean getRandomBoolean(float probability) {
		return random() < probability;
	}
	
	/**
	 * Get a random {@code int} that goes from the value passed as {@code from} to the value passed as {@code to}
	 * with both values included. If {@code to} is greater than {@code from} the two values are switched
	 * @param from Lowest value that the response can be
	 * @param to Highest value that the response can be
	 * @return A random {@code int} that goes from {@code from} to {@code to} (included)
	 */
	public static int getRandomInt(int from, int to) {
		// If the "to" value is lower than "from"'s, switch them
		if(to < from)
		{
			int temp = to;
			to = from;
			from = to;
		}
		
		to++;
		int x = (int) floor((double) random() * (to - from)) + from;
		return x;
	}
	
	public static boolean getRandomBoolean(int good, int total) {
		return getRandomBoolean(((float) good) / ((float) total));
	}
	
	public static boolean getRandomBoolean(float good, float total) {
		return getRandomBoolean(good / total);
	}
	
	public static boolean flipCoin() {
		return getRandomBoolean(0.5f);
	}
	
	public static int tossDie() {
		return getRandomInt(1, 6);
	}
}
