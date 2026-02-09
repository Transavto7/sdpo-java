package ru.nozdratenko.sdpo.controller.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.Termometer.ThermometerResultTask;
import ru.nozdratenko.sdpo.task.Termometer.ThermometerTaskRunner;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ThermometerController {
    private final ThermometerTaskRunner thermometerTaskRunner;

    @Autowired
    public ThermometerController(ThermometerTaskRunner thermometerTaskRunner) {
        this.thermometerTaskRunner = thermometerTaskRunner;
    }

    @PostMapping(value = "/device/thermometer")
    @ResponseBody
    public ResponseEntity thermometer() {
        ThermometerResultTask task = this.thermometerTaskRunner.getThermometerResultTask();

        if (task.exist()) {
            double result = task.result;
            task.clear();
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/thermometer/test")
    @ResponseBody
    public ResponseEntity thermometerTest(@RequestBody Map<String, Object> json) {
        try {
            if (!json.containsKey("temp")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required parameter: temp");
                return ResponseEntity.badRequest().body(error);
            }

            double temp;
            try {
                temp = Double.parseDouble(json.get("temp").toString());
            } catch (NumberFormatException e) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid temperature format. Must be a number.");
                return ResponseEntity.badRequest().body(error);
            }

            // Валидация значения (реальный диапазон температур)
            if (temp < 30.0 || temp > 45.0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Temperature must be between 30.0 and 45.0 °C");
                return ResponseEntity.badRequest().body(error);
            }

            ThermometerResultTask task = this.thermometerTaskRunner.getThermometerResultTask();
            task.setTestValue(temp);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test value set successfully");
            response.put("temp", temp);

            SdpoLog.info(String.format("Test thermometer endpoint called: %.1f °C", temp));

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            SdpoLog.error("Error in thermometer test endpoint: " + e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
