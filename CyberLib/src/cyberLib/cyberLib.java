package cyberLib;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortTimeoutException;
import cyberLib.arduino.ArduinoSerial;
import cyberLib.arduino.BaudRates;
import cyberLib.io.Printer;

import java.awt.desktop.SystemEventListener;
import java.util.PrimitiveIterator;

public class cyberLib {

    boolean ready = false;

    public static void main(String[] args) throws SerialPortTimeoutException {
        new cyberLib().start();
    }

    public void start() throws SerialPortTimeoutException {
        System.out.println("ciao");

        ArduinoSerial serial = new ArduinoSerial("/dev/ttyUSB0", BaudRates.$115200);

        while(serial.available() < 32)
            ;

        byte[] packet = serial.readBytes(32);
        if(packet[0] != 0x30) {
            System.err.println("qualcosa è andato storto");
            System.exit(1);
        }

        System.out.println("pronto");
        serial.clear();
        byte[] message = new byte[32];
        for (int i = 0; i < message.length; i++)
            message[i] = 0;
        message[0] = 0x11;
        message[1] = 0x00;
        message[2] = 0x00;
        message[3] = 0x0a;
        serial.write(message);

        while(serial.available() < 32)
            ;
        packet = serial.readBytes(32);
        Printer.printByteArray(packet);

    }
}
