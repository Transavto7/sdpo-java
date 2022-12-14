package ru.nozdratenko.sdpo.helper;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import ru.nozdratenko.sdpo.exception.AlcometerException;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class AlcometerHelper {
    public static final String PORT = "COM3";

    public static String getResult() throws SerialPortException, UnsupportedEncodingException, AlcometerException {
        SerialPort serialPort = new SerialPort(PORT);
        try {
            serialPort.openPort();
            serialPort.setParams(4800,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.writeString("$STOPSENTECH\r\n", "ascii");
            serialPort.writeString("$STARTSENTECH\r\n", "ascii");

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }

                String result = serialPort.readString();
                if (result == null) {
                    continue;
                }

                result = result.trim();

                if (result.contains("ERR")) {
                    throw new AlcometerException("Ошибка теста, попробуйте еще раз");
                }


                if (result.contains("$R:")) {
                    result = result.replace("$R:", "");
                    String[] split = result.split(",");

                    try {
                        return String.valueOf(Double.valueOf(split[0]));
                    } catch (IllegalArgumentException e) {
                        return split[0];
                    }
                }

            }
        } finally {
            if (serialPort.isOpened()) {
                serialPort.closePort();
            }
        }

    }
}
