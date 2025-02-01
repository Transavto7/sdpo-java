package ru.nozdratenko.sdpo.helper;

import jssc.SerialPort;
import jssc.SerialPortException;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.lib.COMPorts;
import ru.nozdratenko.sdpo.listener.PortListener;
import ru.nozdratenko.sdpo.task.AlcometerResultTask;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;
import ru.nozdratenko.sdpo.util.port.PortManager;

import java.io.UnsupportedEncodingException;

public class AlcometerHelper {
    public static String PORT = null;
    private static SerialPort serialPort = null;
    public static String DEVICE_INSTANCE_ID = Sdpo.systemConfig.getString("alcometer_instance_id");
    private static int ALCOMETER_REINIT_PORT_COUNT = Sdpo.systemConfig.getInt("alcometer_reinit_port_count");
    private static final String VENDOR_ID = "VID_0483";
    private static int reinitPortCount = 0;

    public static void open() throws SerialPortException, AlcometerException, UnsupportedEncodingException {
        SdpoLog.info("Open Alcometer Port...");
        if (PORT == null) {
            SdpoLog.warning("PORT is not defined while open");
            throw new AlcometerException("Порт не найден");
        }

        if (serialPort != null) {
            try {
                serialPort.closePort();
                SdpoLog.info(String.format("Successful close SerialPort %s before open", PORT));
            } catch (SerialPortException ignore) {//ignore
//                SdpoLog.info(String.format("Failed to close SerialPort: %s", serialPort.getPortName()));
            }
        }

        serialPort = new SerialPort(PORT);
        if (!serialPort.isOpened()) {
            SdpoLog.info(String.format("Open SerialPort: %s", PORT));
            try {
                initSerialPort();
            } catch (SerialPortException e){
                SdpoLog.error(e);
                if (e.getExceptionType().contains("Port not found")) {
                    reinitPort(false, true);
                    if (serialPort != null && !serialPort.isOpened()) {
                        try {
                            SdpoLog.info("Retrying to open SerialPort...");
                            initSerialPort();
                            AlcometerResultTask.currentStatus = StatusType.FREE;
                        } catch (SerialPortException e1) {
                            SdpoLog.error("Retry failed. SerialPort is not available.");
                            throw new AlcometerException("Failed to open port after reinit");
                        }
                    }
                }  else {
                    throw e;
                }
            }
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
     * @return boolean
     * @throws SerialPortException
     * @throws UnsupportedEncodingException
     */
    private static boolean enableSlowMode() throws SerialPortException, UnsupportedEncodingException {
        SdpoLog.info("Alcometer send slow mode");
        return getSerialPort().writeString("$STARTSENTECH\r\n", "ascii");
    }

    /**
     * @return boolean
     * @throws SerialPortException
     * @throws UnsupportedEncodingException
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
                rs = Double.parseDouble(split[0]);
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
        SdpoLog.info("Reset Alcometer...");
        SerialPort serialPort = getSerialPort();
        if (serialPort == null) {
            SdpoLog.info(String.format("SerialPort is not defined. ComPort: %s", PORT));
            throw new AlcometerException("Порт не найден");
        }

            if (!serialPort.isOpened()) {
                SdpoLog.info(String.format("Init AlcometerHelper.PORT: %s", PORT));
                SdpoLog.info(String.format("Init SerialPort.Name(): %s", serialPort.getPortName()));
                try {
                    initSerialPort();
                    SdpoLog.info(String.format("Reset alcometer on port %s", PORT));
                    serialPort.writeString("$RESET\r\n", "ascii");
                } catch (SerialPortException | UnsupportedEncodingException e) {
                    SdpoLog.info(String.format("Unable Init SerialPort: %s", PORT));
                    SdpoLog.error(e);
                } finally {
                    if (serialPort.isOpened()) {
                        SdpoLog.info("Close port: " + PORT);
                        try {
                            serialPort.closePort();
                            SdpoLog.info(String.format("\n\tSuccessful close SerialPort %s after reset", PORT));
                        } catch (SerialPortException e) {
                            SdpoLog.info(String.format("\n\tFailed to close SerialPort %s after reset", PORT));
                        }
                    }
                }
            }
    }

    private static void initSerialPort() throws SerialPortException, UnsupportedEncodingException {
        try {
            serialPort.openPort();

            SdpoLog.info("Clean SerialPort buffer...\n" + serialPort.readString());

            serialPort.setParams(4800,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setEventsMask(SerialPort.MASK_RXCHAR |
                    SerialPort.MASK_RXFLAG |
                    SerialPort.MASK_TXEMPTY |
                    SerialPort.MASK_CTS |
                    SerialPort.MASK_DSR |
                    SerialPort.MASK_RLSD |
                    SerialPort.MASK_BREAK |
                    SerialPort.MASK_RING |
                    SerialPort.MASK_ERR);
            serialPort.addEventListener(new PortListener(serialPort));
            SdpoLog.info(String.format("Successful init SerialPort: %s", serialPort.getPortName()));
        } catch (SerialPortException e) {
            SdpoLog.error(String.format("Failed to init SerialPort: %s", serialPort.getPortName()));
            throw new SerialPortException(serialPort, "initSerialPort", e.getExceptionType());
        }
    }

    public static void reinitPort() {
        reinitPort(true, true);
    }
    public static void reinitPort(boolean callSetComPort, boolean checkDeviceInProblemState) {
        boolean reinited = false;
        reinitPortCount++;
        if (DEVICE_INSTANCE_ID != null && reinitPortCount <= 2) {
            SdpoLog.info(reinitPortCount + String.format(". Attempt to reinitialize SerialPort for InstanceId: %s, PORT = %s", DEVICE_INSTANCE_ID, PORT));
            if (PortManager.reinitializePort(DEVICE_INSTANCE_ID, checkDeviceInProblemState)) {
                if (PORT != null && getSerialPort() != null) {
                    if (callSetComPort) setComPort();
//                        try {
                    if (reinitPortCount == 1) {
                        SdpoLog.info("Reinitialized Port: " + PORT);
                        reinitPortCount = 0;
                    } else if (reinitPortCount == 2) {
                        SdpoLog.info("Reset Task.currentStatus");
                        AlcometerResultTask.currentStatus = StatusType.FREE;
                    }
                    reinited = true;
//                    else { //TODO: create logic when setComPort() gets error_unknown response
//                        SdpoLog.info("\nCheck if setComPort() gets error_unknown response");
//                    }
//                        } catch (AlcometerException e) {
//                            SdpoLog.info("\n\tFailed to reinit SerialPort: " + PORT);
//                            SdpoLog.error(e);
//                            AlcometerResultTask.currentStatus = StatusType.FREE;
//                        }
                }

                SdpoLog.info("Current StatusType: " + AlcometerResultTask.currentStatus);
                if (reinited) {
                    ALCOMETER_REINIT_PORT_COUNT++;
                    Sdpo.systemConfig.set("alcometer_reinit_port_count", ALCOMETER_REINIT_PORT_COUNT).saveFile();
                    SdpoLog.info("Succeed reinitialize SerialPort: " + PORT);
                }
            }
        } else {
            SdpoLog.error("Check Alcometer Port Connection !");
        }
    }

    public static void close() throws AlcometerException {
        if (serialPort == null) {
            throw new AlcometerException("Порт не найден");
        }

        try {
            serialPort.closePort();
            SdpoLog.info("Successful close SerialPort: " + PORT);
        } catch (SerialPortException ignore) {}
    }

    public static SerialPort getSerialPort() {
        if (serialPort != null) {
            if (PORT != null && !serialPort.getPortName().equals(PORT)) {
                SdpoLog.info("PORT and serialPort.Name are different: " + PORT + "|" + serialPort.getPortName());
                serialPort = new SerialPort(PORT);
//            PORT = serialPort.getPortName();
//            setComPort(true);
            }
            SdpoLog.info("serialPort.Name: " + serialPort.getPortName() + ", isOpened:" + serialPort.isOpened());
        } else if (PORT != null) {
            SdpoLog.info("serialPort = null, PORT: " + PORT);
            serialPort = new SerialPort(PORT);
        } else {
            SdpoLog.info("serialPort = null, PORT = null");
        }
        return serialPort;
    }

    public static synchronized void setComPort() {
        SdpoLog.info("Request com port alcometer...");
        String alcometerPort = COMPorts.getComPort(VENDOR_ID);
        if (alcometerPort.contains("error")) {
            SdpoLog.error("Alcometer return error: " + alcometerPort);
            reinitPort(false, false);
            reinitPortCount = 0;
//            AlcometerHelper.PORT = null;
        } else if (!alcometerPort.equals(AlcometerHelper.PORT)) {
            if (AlcometerHelper.PORT != null)
                SdpoLog.warning(String.format("Alcometer has a different port: %s as opposed to AlcometerHelper.PORT: %s", alcometerPort, AlcometerHelper.PORT));
            AlcometerHelper.PORT = alcometerPort;
            try {
                AlcometerHelper.reset();
            } catch (AlcometerException ignored) {}
        }

        SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
    }

    public static void setDeviceInstanceId() {
        SdpoLog.info("Request InstanceId alcometer...");
        String deviceInstanceId = PortManager.getDeviceInstanceId(VENDOR_ID);
        if (deviceInstanceId != null) {
            if (DEVICE_INSTANCE_ID == null || !DEVICE_INSTANCE_ID.equals(deviceInstanceId)) {
                SdpoLog.info("Previous Alcometer Instance Id: " + DEVICE_INSTANCE_ID);
                DEVICE_INSTANCE_ID = deviceInstanceId;
                Sdpo.systemConfig.set("alcometer_instance_id", deviceInstanceId).saveFile();
                SdpoLog.info("Alcometer set Instance Id: " + deviceInstanceId);
            }
        } else if (DEVICE_INSTANCE_ID == null) {
            SdpoLog.warning("Can't get Alcometer Instance Id");
        } else {
            reinitPort();
            reinitPortCount = 0;
        }
        SdpoLog.info("Alcometer Reinit Port Count: " + ALCOMETER_REINIT_PORT_COUNT);
    }
}
