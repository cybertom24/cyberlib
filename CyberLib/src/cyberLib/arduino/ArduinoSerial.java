package cyberLib.arduino;

import java.io.*;
import java.util.ArrayList;
import com.fazecast.jSerialComm.*;

/**
 * Trying to make it Win/Linux compatible
 * Win: the USB port is defined as "COM*" where * is the number of the port
 * Linux: USB port are defined as "/dev/ttyUSB*" where * is the number
 */


public class ArduinoSerial implements AutoCloseable {
	
	private final boolean debug = false;
	
	private final SerialPort serialPort;
	private final ArrayList<SerialListener> listeners;
	public final ArrayList<Byte> inputBuffer;
	private final OutputStream outputStream;
	
	/**
	 * 
	 * @param portString
	 * @param baudRate
	 * @throws SerialPortTimeoutException
	 * @throws SerialPortInvalidPortException if no ports are found
	 */
	public ArduinoSerial(String portString, BaudRates baudRate) throws SerialPortTimeoutException {
		listeners = new ArrayList<>();
		inputBuffer = new ArrayList<>();

		serialPort = SerialPort.getCommPort(portString);
		serialPort.setComPortParameters(baudRate.rate, 8, 1, 0); // Default for arduino
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

		if (!serialPort.openPort())
			throw new SerialPortTimeoutException("Port " + portString + " failed to open");
		
		outputStream = serialPort.getOutputStream();

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
		
		addListener(data -> {
			synchronized (inputBuffer) {
				for (byte b : data)
					inputBuffer.add(b);
			}

			if(debug) {
				for(byte b : data)
					System.out.printf("\nArrivato: 0x%x", b);
				System.out.println();
			}
		});
	}

	public OutputStream getOutputStream() {
		return serialPort.getOutputStream();
	}

	public InputStream getInputStream() {
		return serialPort.getInputStream();
	}

	public boolean isOpen() {
		return serialPort.isOpen();
	}

	public void write(byte[] message) {
		try {
			outputStream.write(message);
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * How many bytes are ready to be read
	 * @return
	 */
	public int available() {
		// Richiama la porta così si sveglia e controlla che siano arrivati nuovi pacchetti
		serialPort.bytesAvailable();
		return inputBuffer.size();
	}

	public byte[] readAllBytes() {
		byte[] data = new byte[inputBuffer.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = inputBuffer.get(i);
		}
		inputBuffer.clear();
		return data;
	}

	/**
	 * Tries to read length bytes from the buffer. The bytes read are less or equal to length. Check the length of the array returned
	 * @param length how many bytes to read
	 * @return byte array
	 */
	public byte[] readBytes(int length) {
		length = Math.min(length, inputBuffer.size());
		if (length == 0)
			return new byte[0];

		byte[] data = new byte[length];
		for (int i = 0; i < length; i++) {
			data[i] = inputBuffer.remove(0);
		}
		return data;
	}

	public byte read() {
		if(inputBuffer.isEmpty())
			return 0;
		return inputBuffer.remove(0);
	}

	public void clear() {
		inputBuffer.clear();
	}
	
	public byte[] awaitResponse(byte[] message, int packetLength) {
		write(message);

		while(available() < packetLength)
			;

		return readBytes(packetLength);
	}

	public void close() throws IOException {
		outputStream.close();	
		serialPort.closePort();
	}

	public void addListener(SerialListener listener) {
		listeners.add(listener);
	}
}
