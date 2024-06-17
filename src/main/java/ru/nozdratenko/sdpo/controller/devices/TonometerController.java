package ru.nozdratenko.sdpo.controller.devices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TonometerController {
    @PostMapping(value = "/device/tonometer")
    @ResponseBody
    public ResponseEntity tonometer() throws InterruptedException {
        if (Sdpo.tonometerResultTask.currentStatus == StatusType.RESULT) {
            Map<String, Object> result = new HashMap<>(Sdpo.tonometerResultTask.json.toMap());
            Sdpo.tonometerResultTask.currentStatus = StatusType.STOP;
            return ResponseEntity.ok().body(result);
        }

        if (Sdpo.tonometerResultTask.currentStatus == StatusType.FREE) {
            Sdpo.tonometerResultTask.currentStatus = StatusType.REQUEST;
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/tonometer/connect")
    @ResponseBody
    public ResponseEntity tonometerConnect(@RequestBody Map<String, String> json) throws InterruptedException {
        if (json.containsKey("status") && json.get("status").equals("stop")) {
            Sdpo.tonometerConnectTask.currentStatus = StatusType.STOP;
            SdpoLog.info("Stop connecting tonometer");
            return ResponseEntity.ok().body("stop");
        }

        if (Sdpo.tonometerConnectTask.currentStatus == StatusType.RESULT) {
            SdpoLog.info("set connection tonometer");
            Sdpo.tonometerConnectTask.currentStatus = StatusType.FREE;
            return ResponseEntity.ok().body("set");
        }

        if (Sdpo.tonometerConnectTask.currentStatus == StatusType.FREE) {
            SdpoLog.info("Start connecting tonometer");
            Sdpo.tonometerConnectTask.currentStatus = StatusType.WAIT;
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/tonometer/disable")
    @ResponseBody
    public ResponseEntity tonometerDisable() throws InterruptedException {
        Sdpo.tonometerResultTask.currentStatus = StatusType.STOP;
        return ResponseEntity.ok().body("next");
    }
}
