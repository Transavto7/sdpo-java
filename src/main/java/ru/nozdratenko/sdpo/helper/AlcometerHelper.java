package ru.nozdratenko.sdpo.helper;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.lib.COMPortsServices.COMPorts;
import ru.nozdratenko.sdpo.listener.PortListener;
import ru.nozdratenko.sdpo.task.Alcometer.AlcometerResultTask;
import ru.nozdratenko.sdpo.task.Alcometer.RerunTask.RerunEventPublisher;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.port.PortManager;

import java.io.UnsupportedEncodingException;

@Service
public class AlcometerHelper {
    public static String PORT = null;
    private static SerialPort serialPort = null;
    private static String DEVICE_INSTANCE_ID;
    private static int ALCOMETER_REINIT_PORT_COUNT;
    private static final String VENDOR_ID = "VID_0483";
    private static int reinitPortCount = 0;
    private final PortManager portManager;
    private final RerunEventPublisher rerunEventPublisher;
    private final COMPorts comPorts;

    @Autowired
    public AlcometerHelper(
            PortManager portManager,
            RerunEventPublisher rerunEventPublisher,
            COMPorts comPorts
    ) {
        this.portManager = portManager;
        this.rerunEventPublisher = rerunEventPublisher;
        this.comPorts = comPorts;
    }

    public void init() {
        DEVICE_INSTANCE_ID = Sdpo.settings.systemConfig.getString("alcometer_instance_id");
        ALCOMETER_REINIT_PORT_COUNT = Sdpo.settings.systemConfig.getInt("alcometer_reinit_port_count");
    }

    public void open() throws SerialPortException, AlcometerException, UnsupportedEncodingException {
        SdpoLog.info("Open Alcometer Port...");
        if (PORT == null) {
            SdpoLog.warning("PORT is not defined while open");
            throw new AlcometerException("Порт не найден");
        }

        if (serialPort != null) {
            try {
                serialPort.closePort();
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
                    reinitPort();
                }
            }
        }
    }

    public void stop() throws UnsupportedEncodingException, SerialPortException, AlcometerException {
        if (PORT == null) {
            throw new AlcometerException("Порт не найден");
        }

        SdpoLog.info("Stop alcometer");
        this.getSerialPort().writeString("$STOPSENTECH\r\n", "ascii");
    }

    public void start() throws SerialPortException, UnsupportedEncodingException, AlcometerException {
        if (this.getSerialPort() == null) {
            throw new AlcometerException("Порт не найден");
        }

        boolean currentMod;
        SdpoLog.info("Start alcometer");
        if (Sdpo.settings.systemConfig.getBoolean("alcometer_fast")) {
            currentMod = this.enableFastMode();
        } else {
            currentMod = this.enableSlowMode();
        }
        if (!currentMod) SdpoLog.warning("Режим алкометра не установлен");
    }

    /**
     * @return boolean
     */
    private boolean enableSlowMode() throws SerialPortException, UnsupportedEncodingException {
        SdpoLog.info("Alcometer send slow mode");
        return this.getSerialPort().writeString("$STARTSENTECH\r\n", "ascii");
    }

    /**
     * @return boolean
     */
    private boolean enableFastMode() throws SerialPortException, UnsupportedEncodingException {
        SdpoLog.info("Alcometer send fast mode");
        return this.getSerialPort().writeString("$FASTSENTECH\r\n", "ascii");
    }

    public String result() throws SerialPortException, AlcometerException {
        if (getSerialPort() == null) {
            throw new AlcometerException("Порт не найден");
        }

        String result = getSerialPort().readString();
        if (result == null) {
            this.setComPort();
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

    public void reset() throws AlcometerException {
        SdpoLog.info("Reset Alcometer...");
        SerialPort serialPort = getSerialPort();
        if (serialPort == null) {
            SdpoLog.info(String.format("SerialPort is not defined. ComPort: %s", PORT));
            throw new AlcometerException("Порт не найден");
        }

        try {
            if (!serialPort.isOpened()) {
                SdpoLog.info(String.format("Init SerialPort: %s", PORT));
                if (initSerialPort()) {
                    SdpoLog.info(String.format("Reset alcometer on port %s", PORT));
                    serialPort.writeString("$RESET\r\n", "ascii");
                } else {
                    SdpoLog.info(String.format("Unable Init SerialPort: %s", PORT));
                }
            }

        } catch (SerialPortException | UnsupportedEncodingException e) {
            SdpoLog.error(e);
        } finally {
            if (serialPort.isOpened() && reinitPortCount == 0) {
                SdpoLog.info("Close port: " + PORT);
                try {
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    //
                }
            }
        }
    }

    private static boolean initSerialPort() throws SerialPortException, UnsupportedEncodingException {
        boolean succeed = false;
        try {
            serialPort.openPort();

            String bufferContent = serialPort.readString();
            SdpoLog.info("Clean SerialPort buffer... " + (bufferContent != null && bufferContent.isEmpty() ? "Empty" : "Done"));

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
            PortListener portListener = new PortListener();
            portListener.setPort(serialPort);
            serialPort.addEventListener(portListener);
            SdpoLog.info(String.format("Successful init SerialPort: %s", serialPort.getPortName()));
            succeed = true;
        } catch (SerialPortException e) {
            SdpoLog.error(String.format("Failed to init SerialPort: %s", serialPort.getPortName()));
            throw new SerialPortException(serialPort, "initSerialPort", e.getExceptionType());
        }
        return succeed;
    }

    public void reinitPort() {
        reinitPortCount++;
        if (DEVICE_INSTANCE_ID != null && reinitPortCount <= 5) {
            SdpoLog.info(reinitPortCount + String.format(". Attempt to reinitialize SerialPort for InstanceId: %s, PORT = %s", DEVICE_INSTANCE_ID, PORT));
            suspendCurrentAction(1000L * reinitPortCount);
            if (this.portManager.reinitializePort(DEVICE_INSTANCE_ID)) {
                    PORT = null;
                    serialPort = null;
                    setComPort();
                    if (getSerialPort()!=null) {
                        Sdpo.settings.systemConfig.set("alcometer_reinit_port_count", ++ALCOMETER_REINIT_PORT_COUNT).saveFile();
                        SdpoLog.info("reinitPort|Succeed reinitialize SerialPort: " + getSerialPort().getPortName());
                        SdpoLog.info("reinitPort|Current StatusType: " + AlcometerResultTask.currentStatus);
                    }
            }
        } else {
            SdpoLog.error("reinitPort|Check Alcometer Port Connection !");
        }
    }

    private void suspendCurrentAction(long downtime){
        try {
            Thread.sleep(downtime);
        } catch (InterruptedException e){
            SdpoLog.error("suspendCurrentAction: " + e);
        }
    }

    public void close() throws AlcometerException {
        if (serialPort == null) {
            throw new AlcometerException("Порт не найден");
        }

        try {
            serialPort.closePort();
        } catch (SerialPortException ignore) {

        }
    }

    public SerialPort getSerialPort() {
        if (serialPort != null) {
//            SdpoLog.info("getSerialPort: " + serialPort.getPortName() + ", isOpened:" + serialPort.isOpened());
        } else if (PORT != null) {
            serialPort = new SerialPort(PORT);
        }
        return serialPort;
    }

    public synchronized void setComPort() {
        SdpoLog.info("Request com port alcometer...");
        String alcometerPort = this.comPorts.getComPort(VENDOR_ID);
        if (alcometerPort.contains("error")) {
            AlcometerHelper.PORT = null;
            SdpoLog.error("Alcometer return error: " + alcometerPort);
        } else if (!alcometerPort.equals(AlcometerHelper.PORT)) {
            if (AlcometerHelper.PORT != null)
                SdpoLog.warning(String.format("Alcometer has a different port: %s as opposed to AlcometerHelper.PORT: %s", alcometerPort, AlcometerHelper.PORT));
            AlcometerHelper.PORT = alcometerPort;
            try {
                this.reset();
            } catch (AlcometerException ignored) {}
        }

        SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
    }

    public void setDeviceInstanceId() {
        SdpoLog.info("Request Alcometer InstanceId ...");
        String deviceInstanceId = this.portManager.getDeviceInstanceId(VENDOR_ID);
        if (deviceInstanceId != null) {
            SdpoLog.info("Alcometer port is available. Instance Id: " + deviceInstanceId);
            if (DEVICE_INSTANCE_ID == null || !DEVICE_INSTANCE_ID.equals(deviceInstanceId)) {
                DEVICE_INSTANCE_ID = deviceInstanceId;
                Sdpo.settings.systemConfig.set("alcometer_instance_id", deviceInstanceId).saveFile();
                SdpoLog.info("Set Alcometer Instance Id: " + deviceInstanceId);
            }
        } else if (DEVICE_INSTANCE_ID == null) {
            SdpoLog.warning("Can't get Alcometer Instance Id");
        } else {
            reinitPort();
        }
        SdpoLog.info("Alcometer Reinit Port Count: " + ALCOMETER_REINIT_PORT_COUNT);
    }
}
