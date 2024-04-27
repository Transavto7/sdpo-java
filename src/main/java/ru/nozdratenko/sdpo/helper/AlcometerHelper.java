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

    public static void open() throws SerialPortException, AlcometerException {
        if (PORT == null) {
            throw new AlcometerException("Порт не найден");
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

    public static void stop() throws UnsupportedEncodingException, SerialPortException, AlcometerException {
        if (PORT == null) {
            throw new AlcometerException("Порт не найден");
        }

        SdpoLog.info("Stop alcometer");
        getSerialPort().writeString("$STOPSENTECH\r\n", "ascii");
    }

    public static void start() throws SerialPortException, UnsupportedEncodingException, AlcometerException {
        if (getSerialPort() == null) {
            throw new AlcometerException("Порт не найден");
        }

        boolean currentMod;
        SdpoLog.info("Start alcometer");
        if (Sdpo.systemConfig.getBoolean("alcometer_fast")) {
            currentMod = enableFastMode();
        } else {
            currentMod = enableSlowMode();
        }
        if (!currentMod) SdpoLog.warning("Режим алкометра не установлен");
    }

    /**
     * @throws SerialPortException
     * @throws UnsupportedEncodingException
     * @return boolean
     */
    private static boolean enableSlowMode() throws SerialPortException, UnsupportedEncodingException {
        SdpoLog.info("Alcometer send slow mode");
        return getSerialPort().writeString("$STARTSENTECH\r\n", "ascii");
    }

    /**
     * @throws SerialPortException
     * @throws UnsupportedEncodingException
     * @return boolean
     */
    private static boolean enableFastMode() throws SerialPortException, UnsupportedEncodingException {
        SdpoLog.info("Alcometer send fast mode");
        return getSerialPort().writeString("$FASTSENTECH\r\n", "ascii");
    }

    public static String result() throws SerialPortException, AlcometerException {
        if (getSerialPort() == null) {
            throw new AlcometerException("Порт не найден");
        }

        String result = getSerialPort().readString();
        if (result == null) {
            AlcometerHelper.setComPort();
            return null;
        }

        result = result.trim();

        if (result.contains("$STANBY")) {
           return "STATUS_READY";
        }
        if (result.contains("$BREATH") || result.contains("$TRIGGER")) {
            return "ANALYSE";
        }

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

    public static void reset() throws AlcometerException {
        SerialPort serialPort = getSerialPort();
        if (serialPort == null) {
            throw new AlcometerException("Порт не найден");
        }

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

    public static void close() throws AlcometerException {
        if (serialPort == null) {
            throw new AlcometerException("Порт не найден");
        }

        try {
            serialPort.closePort();
        } catch (SerialPortException ignore) {

        }
    }

    public static SerialPort getSerialPort() {
        if (serialPort == null && PORT != null) {
            serialPort = new SerialPort(PORT);
        } if (PORT == null) {
            serialPort = null;
        }

        return serialPort;
    }

    public static synchronized void setComPort() {
        SdpoLog.info("Request com port alcometer...");
        String alcometerPort = COMPorts.getComPort("VID_0483");
        if (alcometerPort.contains("error")) {
            AlcometerHelper.PORT = null;
            SdpoLog.error("Alcometer return error: " + alcometerPort);
        } else if (!alcometerPort.equals(AlcometerHelper.PORT)) {
            AlcometerHelper.PORT = alcometerPort;
            try {
                AlcometerHelper.reset();
            } catch (AlcometerException ignored) {}
        }

        SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
    }
}
