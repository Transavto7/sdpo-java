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
}
