package cyberLib.arduino;

public interface SerialListener {
	public void onDataArrived(byte[] data);
}
