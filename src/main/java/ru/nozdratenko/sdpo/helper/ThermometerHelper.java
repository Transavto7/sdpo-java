package ru.nozdratenko.sdpo.helper;

import jssc.*;
import ru.nozdratenko.sdpo.util.SdpoLog;

public class ThermometerHelper {
    public static String PORT = "COM5";
    public static double getTemp() throws SerialPortException {
        SerialPort serialPort = new SerialPort(PORT);

        for (String key: SerialPortList.getPortNames()) {
            SdpoLog.info(key);
        }

        for (int i = 0; i < 3; i++) {
            try {
                SdpoLog.info("Listen port: " + PORT);
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
                if (temp > 32 && temp < 43) {
                    SdpoLog.info("Result temp: " + temp);
                    return temp;
                }
            } catch (SerialPortTimeoutException e) {
                System.out.println("time out thermometer");
            } finally {
                if (serialPort.isOpened()) {
                    SdpoLog.info("Close port: " + PORT);
                    serialPort.closePort();
                }
            }
        }

        return 0.0;
    }
}
