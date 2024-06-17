package ru.nozdratenko.sdpo.handler;

import jssc.SerialPort;
import jssc.SerialPortException;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

public class PortEventHandler {

    public static void handlePortEvent(SerialPort serialPort) {
        try {
            if (serialPort.isOpened()) {
                serialPort.closePort();
            }
            Thread.sleep(1000);

            serialPort.openPort();

            serialPort.setParams(4800,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // Очистка буферов
            serialPort.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);

            SdpoLog.info("Port restarted successfully");

            // Отправить команду на перезагрузку устройства
            AlcometerHelper.reset();

        } catch (SerialPortException | InterruptedException ex) {
            SdpoLog.error("Failed to restart the port: " + ex.getMessage());
        } catch (AlcometerException e) {
            throw new RuntimeException(e);
        }
    }
}
