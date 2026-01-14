package ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers;

import com.fazecast.jSerialComm.SerialPort;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.port.PortDetectorService.PortDetector;

import java.io.IOException;
import java.util.Scanner;

public class SerialEnvironmentMonitor extends EnvironmentMonitor {

    private SerialPort serialPort;

    public SerialEnvironmentMonitor() {
        super("AUTO");
    }

    public SerialEnvironmentMonitor(byte deviceId) {
        super("AUTO", deviceId);
    }

    @Override
    public boolean connect() throws Exception {
        try {
            String detectedPort = PortDetector.scanForDevice();

            if (detectedPort == null) {
                throw new IOException("Не удалось определить порт устройства");
            }

            portName = detectedPort;

            serialPort = SerialPort.getCommPort(portName);
            serialPort.setBaudRate(EnvironmentMonitor.BAUD_RATE);
            serialPort.setNumDataBits(EnvironmentMonitor.DATA_BITS);
            serialPort.setNumStopBits(EnvironmentMonitor.STOP_BITS);
            serialPort.setParity(EnvironmentMonitor.PARITY);
            serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                1000,
                0
            );

            if (serialPort.openPort()) {
                setInputStream(serialPort.getInputStream());
                setOutputStream(serialPort.getOutputStream());
                SdpoLog.info("EnvironmentMonitor - Успешно подключено к " + portName);

                return true;
            } else {
                throw new IOException("Не удалось открыть порт: " + portName);
            }

        } catch (Exception e) {
            SdpoLog.error("EnvironmentMonitor - Ошибка подключения: " + e.getMessage());
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }
            throw e;
        }
    }

    @Override
    public void disconnect() {
        try {
            super.disconnect();
        } finally {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }
        }
    }
}
