package cyberLib.io;

public class Printer {
	
	public static String BYTE_ARRAY_FORMAT = "[%d] 0x%02x\n";
	
	public static void printByteArray(byte[] array) {
		for(int i = 0; i < array.length; i++) 
			System.out.printf(BYTE_ARRAY_FORMAT, i, array[i]);
	}
	
	public static void printByteArray(char[] array) {
		for(int i = 0; i < array.length; i++) 
			System.out.printf(BYTE_ARRAY_FORMAT, i, (int) array[i]);
	}
	
}
