package ru.nozdratenko.sdpo.util.port.PortDetectorService;

import com.fazecast.jSerialComm.SerialPort;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.ArrayList;
import java.util.List;

public class PortDetector {
    /**
     * Получить список всех доступных COM-портов
     */
    public static List<String> getAllComPorts() {
        List<String> ports = new ArrayList<>();
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        for (SerialPort port : serialPorts) {
            ports.add(port.getSystemPortName());
            SdpoLog.info("PortDetector - Найден порт: " + port.getSystemPortName() +
                " - " + port.getDescriptivePortName());
        }

        return ports;
    }

    /**
     * Автоопределение порта устройства по признакам
     */
    public static String autoDetectDevicePort() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        for (SerialPort port : serialPorts) {
            String description = port.getDescriptivePortName().toLowerCase();

            if (description.contains("usb serial") ||
                description.contains("ch340") ||
                description.contains("cp210") ||
                description.contains("ftdi") ||
                description.contains("uart")) {

                SdpoLog.info("PortDetector - Возможно это наше устройство: " +
                    port.getSystemPortName() + " - " + description);
                return port.getSystemPortName();
            }
        }

        return null;
    }

    /**
     * Проверить, есть ли на порту наше устройство
     */
    public static boolean isOurDeviceOnPort(String portName) {
        try {
            SerialPort port = SerialPort.getCommPort(portName);
            port.setBaudRate(115200);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);

            if (port.openPort()) {
                try {
                    byte[] testCommand = {0x24, 0x01, 0x00, 0x00, 0x2A};
                    port.writeBytes(testCommand, testCommand.length);

                    Thread.sleep(100);

                    byte[] buffer = new byte[1024];
                    int bytesRead = port.readBytes(buffer, buffer.length);

                    if (bytesRead > 0) {
                        String response = new String(buffer, 0, bytesRead);
                        if (response.contains("EM,")) {
                            SdpoLog.info("PortDetector - Устройство найдено на порту " + portName);
                            return true;
                        }
                    }

                } finally {
                    port.closePort();
                }
            }
        } catch (Exception e) {
            // ignore errors
        }

        return false;
    }

    /**
     * Автоматическое сканирование всех портов для поиска устройства
     */
    public static String scanForDevice() {
        SdpoLog.info("PortDetector - Сканирование COM-портов...");
        SerialPort[] serialPorts = SerialPort.getCommPorts();

        for (SerialPort port : serialPorts) {
            String portName = port.getSystemPortName();
            SdpoLog.info("PortDetector - Проверка порта " + portName + "... ");

            if (isOurDeviceOnPort(portName)) {
                SdpoLog.info("PortDetector - УСТРОЙСТВО НАЙДЕНО!");
                return portName;
            } else {
                SdpoLog.info("PortDetector - нет");
            }
        }

        SdpoLog.info("PortDetector - Устройство не найдено на доступных портах");
        return null;
    }
}
