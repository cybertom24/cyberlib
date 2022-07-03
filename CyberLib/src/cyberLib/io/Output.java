package cyberLib.io;

public class Output {
	public enum DecorationType {
		DASH,
		HASHTAG,
		ASTERISK
	}
	
	public static void printDecoration(DecorationType type) {
		System.out.println(decoration(type));
	}
	
	public static String decoration(DecorationType type) {	
		String decoration = "";
		switch(type) {
		case DASH:
			decoration = "------------------------------------";
			break;
		case HASHTAG:
			decoration = "####################################";
			break;
		case ASTERISK:
			decoration = "************************************";
			break;
		}
		return decoration;
	}
}
