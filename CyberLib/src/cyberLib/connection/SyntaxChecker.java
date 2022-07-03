/**
 * 
 */
package cyberLib.connection;

/**
 * Static class that contains methods to verify the validity of IP addresses,
 * port numbers, MAC addresses etc..
 * 
 * @author savol
 *
 */
public class SyntaxChecker {

	protected static final char[] m_VALID_IP_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' };
	protected static final char[] m_VALID_MAC_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
			'D', 'E', 'F', ':' };

	public static final String VALID_IP_CHAR = new String(m_VALID_IP_CHAR);
	public static final String VALID_MAC_CHAR = new String(m_VALID_MAC_CHAR);

	/**
	 * Check if the a String represents a valid IP address
	 * 
	 * @param ip The IP String to be verified
	 * @return {@code true} if the IP String is syntactically correct
	 */
	public static boolean validIP(String ip) {
		// Check if the string contains any invalid character
		for (int i = 0; i < ip.length(); i++) {
			char c = ip.charAt(i);
			if (!VALID_IP_CHAR.contains(c + ""))
				return false;
		}

		// Get all four numbers
		int lastDot = 0;
		for (int i = 0; i < 4; i++) {
			String number = "";
			// Get the position of the next dot
			int nextDot = ip.indexOf('.', lastDot);
			// If there are no more dots (nextDot = -1)
			if (nextDot == -1) {
				// And the last number has not been reached yet
				// it means there are less than 4 numbers, so the IP is invalid
				if (i != 3)
					return false;
				// Otherwise the last number is reached
				number = ip.substring(lastDot);
			} 
			else if(i == 3)
				// When i == 3, lastDot MUST be -1 or it means there are more than 4 numbers
				return false;
			else
				number = ip.substring(lastDot, nextDot);
			
			// Parse the integer
			int x;
			try {
				x = Integer.parseInt(number);
			} catch (NumberFormatException e) {
				// The number is not parsable
				return false;
			}
			
			if(x < 0 || x > 255)
				return false;
			
			lastDot = nextDot + 1;
		}

		return true;
	}
	
	/**
	 * Check if the number passed as argument is a valid IP port
	 * 
	 * @param port int to be checked
	 * @return {@code true} if the port is between 0 and 65535
	 */
	public static boolean validPort(int port) {
		return (port >= 0 && port <= 65535);
	}
	
}
