package cyberLib.arduino;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.fazecast.jSerialComm.*;

public class ArduinoSerial implements AutoCloseable {
	
	private boolean debug = false;
	
	private SerialPort serialPort;
	private ArrayList<SerialListener> listeners;
	private ArrayList<byte[]> buffer;
	private BufferedOutputStream outputStream;
	
	private boolean waiting = false;
	//private byte toFind = 0;
	private Object objHalted;
	
	/**
	 * 
	 * @param portString
	 * @param baudRate
	 * @throws SerialPortTimeoutException
	 * @throws SerialPortInvalidPortException if no ports are found
	 */
	public ArduinoSerial(String portString, BaudRates baudRate) throws SerialPortTimeoutException {
		listeners = new ArrayList<>();
		buffer = new ArrayList<>();

		serialPort = SerialPort.getCommPort(portString);
		serialPort.setComPortParameters(baudRate.rate, 8, 1, 0); // Default for arduino
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

		if (!serialPort.openPort())
			throw new SerialPortTimeoutException("Port " + portString + " failed to open");
		
		outputStream = new BufferedOutputStream(serialPort.getOutputStream());

		serialPort.addDataListener(new SerialPortDataListener() {

			@Override
			synchronized public void serialEvent(SerialPortEvent arg0) {
				byte[] data = arg0.getReceivedData();
				for (SerialListener listener : listeners)
					listener.onDataArrived(data);
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED | SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}
		});
		
		addListener(new SerialListener() {
			
			@Override
			public void onDataArrived(byte[] data) {
				buffer.add(data);
				
				if(waiting /*&& data[0] == toFind*/) {
					// Release the waiting thread
					synchronized (objHalted) {
						objHalted.notify();
						waiting = false;
					}
				}
				
				if(debug) {
					for(byte b : data) 
						System.out.printf("\nArrivato: 0x%x", b);
					System.out.println();
				}
			}
		});
	}
	
	public boolean isOpen() {
		return serialPort.isOpen();
	}
	
	/**
	 * @implNote Use with caution! No checks for null pointer or port closed are
	 *           made!
	 * @param data
	 */
	private void _writeSingle(byte data) {
		try {
			outputStream.write(data);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void _writeMultiple(byte[] data) {
		try {
			outputStream.write(data);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void _addToBuffer(byte data) {
		try {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void _flush() {
		try {
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(byte b) {
		_writeSingle(b);
	}

	public void write(int i) {
		Integer x = i;
		write(x.byteValue());
	}

	public void write(byte[] data) {
		_writeMultiple(data);
	}
	
	public void write(Byte[] data) {
		for(byte b : data)
			_addToBuffer(b);
		_flush();
	}

	public void write(int[] data) {
		for (Integer i : data)
			_addToBuffer(i.byteValue());
		_flush();
	}

	public void write(char c) {
		write((byte) c);
	}

	public void write(char[] data) {
		for (char c : data)
			_addToBuffer((byte) c);
		_flush();
	}

	public void write(String s) {
		char[] charArray = s.toCharArray();
		for (char c : charArray)
			_addToBuffer((byte) c);
		_flush();
	}
	
	public void write(ArrayList<Byte> list) {
		for(int i = 0; i < list.size(); i++)
			_addToBuffer(list.get(i));
		_flush();
	}

	public boolean available() {
		return !buffer.isEmpty();
	}

	public byte[] readBytes() {
		byte[] data = null;
		if(!buffer.isEmpty()) {
			data = buffer.get(0);
			buffer.remove(0);
		}
		return data;
	}
	
	/**
	 * Blocks the thread until a certain byte gets sent over the serial. Use it with caution!
	 * @param toFind The byte to look for
	 * @param who The object who needs to be halted
	 */
	public void waitFor(byte toFind, Object who) {
		waiting = true;
		//this.toFind = toFind;
		objHalted = who;
		
		// Get the lock on the object to halt
		synchronized (objHalted) {
			try {
				// Let it wait
				objHalted.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				waiting = false;
			}
		}
	}
	
	public void waitFor(Object who) {
		waiting = true;
		objHalted = who;
		
		// Get the lock on the object to halt
		synchronized (objHalted) {
			try {
				// Let it wait
				objHalted.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				waiting = false;
			}
		}
	}
	
	public byte[] awaitResponse(byte[] message) {
		write(message);
		waitFor(Thread.currentThread());
		return readBytes();
	}

	public void close() throws IOException {
		outputStream.close();	
		serialPort.closePort();
	}

	public void addListener(SerialListener listener) {
		listeners.add(listener);
	}
}
