package ru.nozdratenko.sdpo.helper;

import jssc.SerialPort;
import jssc.SerialPortException;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.lib.COMPorts;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.UnsupportedEncodingException;

public class AlcometerHelper {
    public static String PORT = null;
    private static SerialPort serialPort = null;

    public static void open() throws SerialPortException {
        if (PORT == null) {
            return;
        }

        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException ignore) {

            }
        }

        serialPort = new SerialPort(PORT);
        if (!serialPort.isOpened()) {
            serialPort.openPort();
            serialPort.setParams(4800,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }
    }

    public static void stop() throws UnsupportedEncodingException, SerialPortException {
        if (PORT == null) {
            return;
        }

        SdpoLog.info("Stop alcometer");
        getSerialPort().writeString("$STOPSENTECH\r\n", "ascii");
    }

    public static void start() throws SerialPortException, UnsupportedEncodingException {
        if (PORT == null) {
            return;
        }

        SdpoLog.info("Start alcometer");
        if (Sdpo.systemConfig.getBoolean("alcometer_fast")) {
            SdpoLog.info("Alcometer send fast mode");
            getSerialPort().writeString("$FASTSENTECH\r\n", "ascii");
        } else {
            SdpoLog.info("Alcometer send slow mode");
            getSerialPort().writeString("$STARTSENTECH\r\n", "ascii");
        }
    }

    public static String result() throws SerialPortException, AlcometerException {
        if (PORT == null) {
            return null;
        }

        String result = getSerialPort().readString();

        if (result == null) {
            return null;
        }

        result = result.trim();

        if (result.contains("$FLOW,ERR")) {
            throw new AlcometerException("Ошибка теста, попробуйте еще раз");
        }

        if (result.contains("$END")) {
            throw new AlcometerException(true);
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
            return String.valueOf(rs);
        }

        return null;
    }

    public static void reset() {
        if (getSerialPort() == null) {
            return;
        }

        SerialPort serialPort = getSerialPort();
        try {
            serialPort.openPort();
            serialPort.setParams(4800,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            SdpoLog.info("Reset alcometer");
            serialPort.writeString("$RESET\r\n", "ascii");
        } catch (SerialPortException | UnsupportedEncodingException e) {
            SdpoLog.error(e);
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

    public static void close() {
        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException ignore) {

            }
        }
    }

    public static SerialPort getSerialPort() {
        if (serialPort == null && PORT != null) {
            serialPort = new SerialPort(PORT);
        }
        return serialPort;
    }

    public static void setComPort() {
        SdpoLog.info("Request com port alcometer...");
        String alcometerPort = COMPorts.getComPort("VID_0483");
        if (alcometerPort.contains("error")) {
            AlcometerHelper.PORT = null;
            SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
        } else if (!alcometerPort.equals(AlcometerHelper.PORT)) {
            AlcometerHelper.PORT = alcometerPort;
            SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
            AlcometerHelper.reset();
        }
    }
}
