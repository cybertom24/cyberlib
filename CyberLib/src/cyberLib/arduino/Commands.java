package cyberLib.arduino;

class Commands {
	public final static byte START = 0x69;
	public final static byte STOP = (byte) 0x96;
	public final static byte READY = (byte) 0xFA;
	public final static byte NEW_PACKET = 0x24;
	public final static byte END_OF_TRANSMISSION = (byte) 0xEF;
}