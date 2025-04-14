package ru.nozdratenko.sdpo.listener;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Component
public class PortListener implements SerialPortEventListener {
    @Setter
    SerialPort port;
    @Autowired
    AlcometerHelper alcometerHelper;

    @Override
    public void serialEvent(SerialPortEvent event) {
        boolean needToRestartPort = false;

        if (event.isRXFLAG()) {
            SdpoLog.info("EVENT RXFLAG detected");
        }

        if (event.isTXEMPTY()) {
            SdpoLog.info("EVENT Transmit buffer is empty");// +
        }

        if (event.isCTS()) {
            if (event.getEventValue() == 1) {
                SdpoLog.info("EVENT CTS line is ON");
            } else {
                SdpoLog.info("EVENT CTS line is OFF");
            }
        }

        if (event.isDSR()) {
            if (event.getEventValue() == 1) {
                SdpoLog.info("EVENT DSR line is ON");
            } else {
                SdpoLog.info("EVENT DSR line is OFF");
            }
        }

        if (event.isRLSD()) {
            if (event.getEventValue() == 1) {
                SdpoLog.info("EVENT DCD line is ON (carrier detected)");
            } else {
                SdpoLog.info("EVENT DCD line is OFF (carrier lost)");
            }
        }

        if (event.isBREAK()) {
            SdpoLog.info("EVENT Break signal detected");
        }

        if (event.isERR()) {
            SdpoLog.error("EVENT Serial port error occurred");
            needToRestartPort = true;
        }

        if (event.isRING()) {
            SdpoLog.info("EVENT Ring indicator detected");
        }

        if (needToRestartPort) {
            this.handlePortEvent(port);
        }

    }

    private void handlePortEvent(SerialPort serialPort) {
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
            this.alcometerHelper.reset();

        } catch (SerialPortException | InterruptedException ex) {
            SdpoLog.error("Failed to restart the port: " + ex.getMessage());
        } catch (AlcometerException e) {
            throw new RuntimeException(e);
        }
    }
}
