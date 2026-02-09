package ru.nozdratenko.sdpo.controller.devices;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.Tonometer.TonometerTaskRunner;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TonometerController {
    @Qualifier("tonometerTaskExecutor")
    private final TonometerTaskRunner tonometerTaskRunner;

    @Autowired
    public TonometerController(TonometerTaskRunner tonometerTaskRunner) {
        this.tonometerTaskRunner = tonometerTaskRunner;
    }

    @PostMapping(value = "/device/tonometer")
    @ResponseBody
    public ResponseEntity tonometer() throws InterruptedException {
        if (this.tonometerTaskRunner.getTonometerResultTask().getCurrentStatus() == StatusType.RESULT) {
            JSONObject json = this.tonometerTaskRunner.getTonometerResultTask().getJson();
            SdpoLog.info("!!! TonometerController.tonometer.tonometerResultTask.json: " + json);
            Map<String, Object> result = new HashMap<>(json.toMap());
            this.tonometerTaskRunner.getTonometerResultTask().setCurrentStatus(StatusType.STOP);
            return ResponseEntity.ok().body(result);
        }

        if (this.tonometerTaskRunner.getTonometerResultTask().getCurrentStatus() == StatusType.FREE) {
            this.tonometerTaskRunner.getTonometerResultTask().setCurrentStatus(StatusType.REQUEST);
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/tonometer/connect")
    @ResponseBody
    public ResponseEntity tonometerConnect(@RequestBody Map<String, String> json) throws InterruptedException {
        SdpoLog.info("!!! TonometerController.tonometerConnect.json: " + json);
        if (json.containsKey("status") && json.get("status").equals("stop")) {
            this.tonometerTaskRunner.getTonometerConnectTask().currentStatus = StatusType.STOP;
            SdpoLog.info("Stop connecting tonometer");
            return ResponseEntity.ok().body("stop");
        }

        if (this.tonometerTaskRunner.getTonometerConnectTask().currentStatus == StatusType.RESULT) {
            SdpoLog.info("set connection tonometer");
            this.tonometerTaskRunner.getTonometerConnectTask().currentStatus = StatusType.FREE;
            return ResponseEntity.ok().body("set");
        }

        if (this.tonometerTaskRunner.getTonometerConnectTask().currentStatus == StatusType.FREE) {
            SdpoLog.info("Start connecting tonometer");
            this.tonometerTaskRunner.getTonometerConnectTask().currentStatus = StatusType.WAIT;
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/tonometer/disable")
    @ResponseBody
    public ResponseEntity tonometerDisable() throws InterruptedException {
        this.tonometerTaskRunner.getTonometerResultTask().setCurrentStatus(StatusType.STOP);
        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/tonometer/test")
    @ResponseBody
    public ResponseEntity tonometerTest(@RequestBody Map<String, Integer> json) {
        try {
            if (!json.containsKey("systolic") || !json.containsKey("diastolic") || !json.containsKey("pulse")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required parameters: systolic, diastolic, pulse");
                return ResponseEntity.badRequest().body(error);
            }

            int systolic = json.get("systolic");
            int diastolic = json.get("diastolic");
            int pulse = json.get("pulse");

            // Валидация значений
            if (systolic < 50 || systolic > 250) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Systolic pressure must be between 50 and 250");
                return ResponseEntity.badRequest().body(error);
            }

            if (diastolic < 30 || diastolic > 150) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Diastolic pressure must be between 30 and 150");
                return ResponseEntity.badRequest().body(error);
            }

            if (pulse < 30 || pulse > 200) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Pulse must be between 30 and 200");
                return ResponseEntity.badRequest().body(error);
            }

            this.tonometerTaskRunner.getTonometerResultTask().setTestValues(systolic, diastolic, pulse);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test values set successfully");
            response.put("systolic", systolic);
            response.put("diastolic", diastolic);
            response.put("pulse", pulse);

            SdpoLog.info(String.format("Test tonometer endpoint called: %d/%d, pulse: %d", systolic, diastolic, pulse));

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            SdpoLog.error("Error in tonometer test endpoint: " + e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
