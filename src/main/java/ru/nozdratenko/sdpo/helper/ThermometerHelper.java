package ru.nozdratenko.sdpo.helper;

import jssc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.lib.COMPortsServices.COMPorts;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Service
public class ThermometerHelper {
    public static String PORT = null;
    private final COMPorts comPorts;

    @Autowired
    public ThermometerHelper(COMPorts comPorts) {
        this.comPorts = comPorts;
    }

    public double getTemp() {
        if (PORT == null) {
            return 0.0;
        }

        SerialPort serialPort = new SerialPort(PORT);

        try {
            serialPort.openPort();
            serialPort.setParams(2400,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                SerialPort.FLOWCONTROL_RTSCTS_OUT);

            int[] receivedData = serialPort.readIntArray(8, 3000);
            double temp = receivedData[5] + 256;
            temp /= 10;

            if (receivedData[6] != 1) {
                SdpoLog.info("Lo temp: " + receivedData[6]);
                return 0.0;
            }

            SdpoLog.info("Result temp: " + temp);
            return temp;
        } catch (SerialPortTimeoutException | SerialPortException e) {
            //
        } finally {
            if (serialPort.isOpened()) {
                try {
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    SdpoLog.error(e);
                }
            }
        }

        return 0.0;
    }

    public void setComPort() {
        SdpoLog.info("Request com port thermometer...");
        String thermometerPort = this.comPorts.getComPort("VID_10C4");

        if (thermometerPort.contains("error")) {
            ThermometerHelper.PORT = null;
            SdpoLog.info("Thermometer set port: " + ThermometerHelper.PORT);
        } else if (!thermometerPort.equals(ThermometerHelper.PORT)) {
            ThermometerHelper.PORT = thermometerPort;
            SdpoLog.info("Thermometer set port: " + ThermometerHelper.PORT);
        }
    }
}
