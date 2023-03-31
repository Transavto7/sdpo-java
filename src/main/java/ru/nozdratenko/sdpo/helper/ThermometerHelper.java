package ru.nozdratenko.sdpo.helper;

import jssc.*;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.Arrays;

public class ThermometerHelper {
    public static String PORT = null;
    public static double getTemp() {
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

            if (receivedData[6] != 1 || temp < 32 || temp > 42) {
                SdpoLog.info("Lo temp: " + temp);
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
}
