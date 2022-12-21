package ru.nozdratenko.sdpo.helper;

import jssc.SerialPort;
import jssc.SerialPortException;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.UnsupportedEncodingException;

public class AlcometerHelper {
    public static String PORT = "COM3";

    public static double getResult() throws SerialPortException, UnsupportedEncodingException, AlcometerException {
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

            if (Sdpo.systemConfig.getBoolean("alcometer_fast")) {
                SdpoLog.info("Alcometer send fast mode");
                serialPort.writeString("$FASTSENTECH\r\n", "ascii");
            } else {
                SdpoLog.info("Alcometer send slow mode");
                serialPort.writeString("$STARTSENTECH\r\n", "ascii");
            }

            int seconds = 0;

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }

                if (++seconds > 30) {
                    throw new AlcometerException("Тест алкоголя был прерван");
                }

                String result = serialPort.readString();
                if (result == null) {
                    continue;
                }

                result = result.trim();

                if (seconds > 15 && result.contains("END")) {
                    throw new AlcometerException("Тест алкоголя был прерван");
                }

                if (result.contains("ERR")) {
                    throw new AlcometerException("Ошибка теста, попробуйте еще раз");
                }


                if (result.contains("$R:")) {
                    double rs = 0;
                    result = result.replace("$R:", "");
                    String[] split = result.split(",");

                    try {
                        rs = Double.valueOf(split[0]);
                    } catch (IllegalArgumentException e) {
                        if (split[0].equals("PASS")) {
                            rs = 0;
                        } else if (split[0].equals("FAIL")) {
                            rs = 1;
                        }
                    }

                    SdpoLog.info("Result alcometer: " + rs);
                    return rs;
                }

            }
        } finally {
            if (serialPort.isOpened()) {
                SdpoLog.info("Close port: " + PORT);
                serialPort.closePort();
            }
        }
    }

    public static void reset() {
        SerialPort serialPort = new SerialPort(PORT);
        try {
            SdpoLog.info("Open port " + PORT);
            serialPort.openPort();
            serialPort.setParams(4800,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.writeString("$RESET\r\n", "ascii");
        } catch (SerialPortException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            if (serialPort.isOpened()) {
                SdpoLog.info("Close port: " + PORT);
                try {
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    //
                }
            }
        }
    }
}
