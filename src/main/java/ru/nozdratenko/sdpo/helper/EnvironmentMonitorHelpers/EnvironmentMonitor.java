package ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers;

import lombok.Getter;
import lombok.Setter;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EnvironmentMonitor {
    private static final byte PREAMBLE = 0x24;
    private static final byte TERMINATOR = 0x2A;
    private static final byte CMD_GET_ALL_DATA = 0x00;
    private static final byte CMD_START_STREAMING = 0x02;
    private static final byte CMD_STOP_STREAMING = 0x02;

    public static final int BAUD_RATE = 115200;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = 1;
    public static final int PARITY = 0;

    @Setter
    private InputStream inputStream;
    @Setter
    private OutputStream outputStream;
    protected String portName;
    @Setter
    @Getter
    private byte deviceId = 0x01;
    private final AtomicBoolean isStreaming = new AtomicBoolean(false);
    private Thread streamingThread;

    @Setter
    private Consumer<SensorData> dataCallback;

    public EnvironmentMonitor(String portName) {
        this.portName = portName;
    }

    public EnvironmentMonitor(String portName, byte deviceId) {
        this.portName = portName;
        this.deviceId = deviceId;
    }

    /**
     * Подключение к прибору
     */
    public boolean connect() throws Exception {
        try {
            SdpoLog.info("EnvironmentMonitor - Подключение к " + portName + " (заглушка)");
            SdpoLog.info("EnvironmentMonitor - Параметры: " + BAUD_RATE + " бод, 8N1");

            Thread.sleep(2000);
            SdpoLog.info("EnvironmentMonitor - Устройство готово к работе (синий светодиод)");

            return true;

        } catch (Exception e) {
            SdpoLog.error("EnvironmentMonitor - Ошибка подключения: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Отправка команды на прибор
     */
    private void sendCommand(byte command, byte payload) throws IOException {
        byte[] packet = new byte[5];
        packet[0] = PREAMBLE;           // Преамбула
        packet[1] = deviceId;           // ID устройства
        packet[2] = command;            // Команда
        packet[3] = payload;            // Полезная нагрузка
        packet[4] = TERMINATOR;         // Терминатор

        if (outputStream != null) {
            outputStream.write(packet);
            outputStream.flush();

            SdpoLog.info("EnvironmentMonitor - Отправлена команда: " + bytesToHex(packet));
        }
    }

    /**
     * Получение всех данных однократно (команда 0x00)
     */
    public SensorData getAllData() throws IOException {
        try {
            sendCommand(CMD_GET_ALL_DATA, (byte) 0x00);

            byte[] response = readResponse();

            return parseResponse(response);
        } catch (IOException e) {
            SdpoLog.error("EnvironmentMonitor - Ошибка получения данных: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Начало циклической передачи данных
     */
    public void startStreaming() throws IOException {
        if (!isStreaming.get()) {
            sendCommand(CMD_START_STREAMING, (byte) 0x00);
            isStreaming.set(true);

            // Запуск потока для чтения циклических данных
            streamingThread = new Thread(this::streamingWorker);
            streamingThread.start();

            SdpoLog.info("EnvironmentMonitor - Запущена циклическая передача данных");
        }
    }

    /**
     * Остановка циклической передачи данных
     */
    public void stopStreaming() throws IOException {
        if (isStreaming.get()) {
            sendCommand(CMD_STOP_STREAMING, (byte) 0x00);
            isStreaming.set(false);

            if (streamingThread != null) {
                streamingThread.interrupt();
                try {
                    streamingThread.join(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            SdpoLog.info("EnvironmentMonitor - Циклическая передача остановлена");
        }
    }

    /**
     * Рабочий поток для чтения циклических данных
     */
    private void streamingWorker() {
        while (isStreaming.get() && !Thread.currentThread().isInterrupted()) {
            try {
                byte[] response = readResponse();
                SensorData data = parseResponse(response);

                if (dataCallback != null) {
                    dataCallback.accept(data);
                }

            } catch (IOException e) {
                if (isStreaming.get()) {
                    SdpoLog.error("EnvironmentMonitor - Ошибка чтения потока данных: " + e.getMessage());
                }
                break;
            } catch (Exception e) {
                SdpoLog.error("EnvironmentMonitor - Ошибка обработки данных: " + e.getMessage());
            }
        }
    }

    /**
     * Чтение ответа от устройства
     */
    private byte[] readResponse() throws IOException {
        if (inputStream == null) {
            throw new IOException("Поток ввода не инициализирован");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] tempBuffer = new byte[1024];

        long startTime = System.currentTimeMillis();
        boolean terminatorFound = false;

        while (!terminatorFound && (System.currentTimeMillis() - startTime < 5000)) {
            if (inputStream.available() > 0) {
                bytesRead = inputStream.read(tempBuffer);
                if (bytesRead > 0) {
                    buffer.write(tempBuffer, 0, bytesRead);

                    byte[] data = buffer.toByteArray();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i] == '*') {
                            terminatorFound = true;
                            break;
                        }
                    }
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (!terminatorFound) {
            throw new IOException("Таймаут ожидания ответа");
        }

        return buffer.toByteArray();
    }

    /**
     * Парсинг ответа от устройства
     */
    private SensorData parseResponse(byte[] response) {
        String responseStr = new String(response).trim();

        if (!responseStr.startsWith("$") || !responseStr.endsWith("*")) {
            throw new IllegalArgumentException("Некорректный формат пакета: " + responseStr);
        }

        String data = responseStr.substring(1, responseStr.length() - 1);
        String[] parts = data.split(",");

        if (parts.length < 6) {
            throw new IllegalArgumentException("Некорректное количество данных: " + data);
        }

        SensorData sensorData = new SensorData();

        try {
            sensorData.setDeviceId(Integer.parseInt(parts[0]));

            String packetType = parts[1];
            if (!"EM".equals(packetType)) {
                throw new IllegalArgumentException("Неожиданный тип пакета: " + packetType);
            }

            sensorData.setTemperature(Float.parseFloat(parts[2]));
            sensorData.setPressure(Float.parseFloat(parts[3]));
            sensorData.setHumidity(Float.parseFloat(parts[4]));
            sensorData.setIlluminance(Float.parseFloat(parts[5]));
            sensorData.setTamperDetected("N".equals(parts[6]));
            sensorData.setTimestamp(LocalDateTime.now());

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка парсинга числовых значений", e);
        }

        return sensorData;
    }

    /**
     * Отключение от прибора
     */
    public void disconnect() {
        try {
            if (isStreaming.get()) {
                stopStreaming();
            }

            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }

            SdpoLog.info("EnvironmentMonitor - Отключено от " + portName);

        } catch (IOException e) {
            SdpoLog.error("EnvironmentMonitor - Ошибка при отключении: " + e.getMessage());
        }
    }

    /**
     * Вспомогательный метод для преобразования байтов в hex строку
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex).append(" ");
        }
        return hexString.toString().trim().toUpperCase();
    }

    public boolean isStreaming() { return isStreaming.get(); }
}