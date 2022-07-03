package cyberLib.connection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.IllegalFormatException;

/**
 * Manage a UDP connection
 * 
 * @author savol
 *
 */
public class Udp {
	private final static String BAD_IP_EXCEPTION = "Bad IP format. The IP String must be in the form \"A.B.C.D\" with 0 <= A,B,C,D => 255";
	private final static String BAD_PORT_EXCEPTION = "Bad port format. The port must be between 0 and 65535";
	private final static String BAD_QUEUE_SIZE_EXCEPTION = "Bad queue size format. The queue size must be > 0";
	private final static String BAD_DELAY_EXCEPTION = "Bad delay format. The delay must be >= 0";
	
	public final static String DEFAULT_IP = "192.168.1.1";
	public final static int DEFAULT_PORT = 161;
	public final static int DEFAULT_QUEUE_SIZE = 256;
	public final static int DEFAULT_DELAY = 0;
	
	private int port;
	private String ip = DEFAULT_IP;
	private int maxQueueSize = DEFAULT_QUEUE_SIZE;
	private int delay = DEFAULT_DELAY;
	private ArrayList<byte[]> packetsQueue;
	private boolean going = true;
	/**
	 * Create the UDP object that manages a UDP connection with the device at the IP
	 * address and port given in the constructor arguments
	 * 
	 * @param ip   The IP address of the receiver
	 * @param port The port of the receiver
	 * @throws IllegalArgumentException gets thrown whenever the ip or port passed
	 *                                  are not syntactically correct
	 */
	public Udp(String ip, int port) throws IllegalArgumentException {
		if (SyntaxChecker.validIP(ip))
			this.ip = ip;
		else
			throw new IllegalArgumentException(BAD_IP_EXCEPTION);

		if (SyntaxChecker.validPort(port))
			this.port = port;
		else
			throw new IllegalArgumentException(BAD_PORT_EXCEPTION);

		packetsQueue = new ArrayList<>();
	}
	
	/**
	 * Change maxQueueSize attribute. Keep in mind it cannot be less or equal than 0.
	 * 
	 * @param maxQueueSize The new value for the attribute
	 * @return itself (for methods stacking, better code)
	 * @throws IllegalArgumentException if maxQueueSize is <= 0
	 */
	public Udp setMaxQueueSize(int maxQueueSize) throws IllegalArgumentException {
		if(maxQueueSize <= 0)
			throw new IllegalArgumentException(BAD_QUEUE_SIZE_EXCEPTION);
		
		this.maxQueueSize = maxQueueSize;
		return this;
	}
	
	/**
	 * Change delay attribute. Keep in mind it cannot be less than 0.
	 * 
	 * @param delay The new value for the attribute
	 * @return itself (for methods stacking, better code)
	 * @throws IllegalArgumentException if delay is < 0
	 */
	public Udp setDelay(int delay) throws IllegalArgumentException {
		if(delay < 0)
			throw new IllegalArgumentException(BAD_DELAY_EXCEPTION);
		
		this.delay = delay;
		return this;
	}

	/**
	 * Start the UDP connection on another thread.
	 * If the connection is already going nothing happens.
	 */
	public void start() {
		if (!going) {
			going = true;
			packetsQueue.clear();
			new UdpThread().start();
		}
	}

	/**
	 * Stop the UDP connection.
	 */
	public void stop() {
		going = false;
		packetsQueue.clear();
	}
	
	/**
	 * Get information about the status of the connection
	 * 
	 * @return if the connection is alive
	 */
	public boolean isGoing() {
		return going;
	}
	
	
	/**
	 * Add a packet (byte array) to the queue ready to be sent. If there are too many packets in the queue the current one is discarded.
	 * Set the important flag to indicate if the packet is important.
	 * When an important packet comes, the entire queue is cleared before it gets added to it.
	 * 
	 * @param packet A byte array to be sent to the receiver
	 * @param important	A flag to indicate if the queue is to be cleared before the packets gets added to it
	 */
	public void send(byte[] packet, boolean important) {
		if (going) {
			if (important)
				packetsQueue.clear();
			if (packetsQueue.size() < maxQueueSize)
				packetsQueue.add(packet);
		} else {
			System.err.println("[!] Could not send the message because the socket is closed");
		}
	}
	
	/**
	 * The {@code send(byte[] packet, boolean important)} method but with the {@code important} flag false
	 * 
	 * @param packet The byte array to be sent to the receiver
	 */
	public void send(byte[] packet) {
		send(packet, false);
	}

	class UdpThread extends Thread {

		UdpThread() {
			System.out.printf("[!] Starting UDP client connection to ip: %s and port: %d\n", ip, port);
		}

		@Override
		public void run() {
			try (DatagramSocket socket = new DatagramSocket()) {
				InetAddress address = InetAddress.getByName(ip);

				while (going) {
					if (packetsQueue.size() > 0) {
						byte[] data = packetsQueue.get(0);
						DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
						socket.send(packet);

						packetsQueue.remove(0);
					}
					// System.out.println("Still going...");
					Thread.sleep(delay);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			going = false;
			System.out.println("[!] UDP client stopped");
		}
	}

	public interface UdpListener {
		public void received(DatagramPacket packet);
	}
}
