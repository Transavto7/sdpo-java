package ru.nozdratenko.sdpo.listener;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import ru.nozdratenko.sdpo.handler.PortEventHandler;
import ru.nozdratenko.sdpo.util.SdpoLog;

public class PortListener implements SerialPortEventListener {
    SerialPort port;

    public PortListener(SerialPort port) {
        this.port = port;
    }

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
            PortEventHandler.handlePortEvent(port);
        }

    }
}
