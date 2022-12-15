package ru.nozdratenko.sdpo.helper;

import jssc.SerialPort;
import jssc.SerialPortException;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.UnsupportedEncodingException;

public class AlcometerHelper {
    public static final String PORT = "COM3";

    public static String getResult() throws SerialPortException, UnsupportedEncodingException, AlcometerException {
        SerialPort serialPort = new SerialPort(PORT);
        try {
            SdpoLog.info("Open port " + PORT);
            serialPort.openPort();
            serialPort.setParams(4800,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.writeString("$STOPSENTECH\r\n", "ascii");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                SdpoLog.error(e);
            }

            serialPort.writeString("$STARTSENTECH\r\n", "ascii");
            int seconds = 0;

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }

                if (++seconds > 20) {
                    throw new AlcometerException("Тест алкоголя был прерван");
                }

                String result = serialPort.readString();
                if (result == null) {
                    continue;
                }

                result = result.trim();

                if (seconds > 5 && result.contains("END")) {
                    throw new AlcometerException("Тест алкоголя был прерван");
                }

                if (result.contains("ERR")) {
                    throw new AlcometerException("Ошибка теста, попробуйте еще раз");
                }


                if (result.contains("$R:")) {
                    result = result.replace("$R:", "");
                    String[] split = result.split(",");

                    try {
                        result = String.valueOf(Double.valueOf(split[0]));
                    } catch (IllegalArgumentException e) {
                        result = split[0];
                    }

                    SdpoLog.info("Result alcometer: " + result);
                    return result;
                }

            }
        } finally {
            if (serialPort.isOpened()) {
                SdpoLog.info("Close port: " + PORT);
                serialPort.closePort();
            }
        }
    }
}
