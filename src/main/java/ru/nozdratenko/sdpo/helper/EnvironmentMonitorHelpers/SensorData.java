package ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SensorData {
    private int deviceId;
    private float temperature; // °C
    private float pressure;    // Па
    private float humidity;    // %
    private float illuminance; // лк
    private boolean tamperDetected;
    private LocalDateTime timestamp;

    public float getPressureMmHg() { return pressure * 0.00750062f; } // конвертация в мм рт.ст.

    @Override
    public String toString() {
        return String.format("Device ID: %d, Temp: %.1f °C, Pres: %.0f Pa (%.1f mmHg), " +
                "Hum: %.1f %%, Lux: %.0f lx, Tamper: %s, Time: %s",
            deviceId, temperature, pressure, getPressureMmHg(),
            humidity, illuminance, tamperDetected ? "YES" : "NO",
            timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("temperature", temperature);
        json.put("pressure", pressure);
        json.put("humidity", humidity);
        json.put("illuminance", illuminance);
        json.put("tamper_detected", tamperDetected);

        return json;
    }
}