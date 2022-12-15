package ru.nozdratenko.sdpo.helper;

import jssc.*;
import ru.nozdratenko.sdpo.util.SdpoLog;

public class ThermometerHelper {
    public static final String PORT = "COM5";
    public static double getTemp() throws SerialPortException {
        SerialPort serialPort = new SerialPort(PORT);

        for (String key: SerialPortList.getPortNames()) {
            SdpoLog.debug(key);
        }

        while (true) {
            try {
                SdpoLog.info("Listen port: " + PORT);
                serialPort.openPort();
                serialPort.setParams(2400,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                        SerialPort.FLOWCONTROL_RTSCTS_OUT);

                int[] receivedData = serialPort.readIntArray(8);
                double temp = receivedData[5] + 256;
                temp /= 10;
                if (temp > 32 && temp < 43) {
                    SdpoLog.info("Result temp: " + temp);
                    return temp;
                }
            } finally {
                if (serialPort.isOpened()) {
                    SdpoLog.info("Close port: " + PORT);
                    serialPort.closePort();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
