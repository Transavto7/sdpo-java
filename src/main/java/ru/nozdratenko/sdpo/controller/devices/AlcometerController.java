package ru.nozdratenko.sdpo.controller.devices;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.Alcometer.AlcometerResultTask;
import ru.nozdratenko.sdpo.task.Alcometer.AlcometerTaskRunner;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;
import ru.nozdratenko.sdpo.websocket.AlcometrStatusEndPoint;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

@RestController
public class AlcometerController {
    private  final AlcometerTaskRunner alcometerTaskRunner;

    public AlcometerController(AlcometerTaskRunner alcometerTaskRunner) {
        this.alcometerTaskRunner = alcometerTaskRunner;
    }

    @PostMapping(value = "/device/alcometer")
    @ResponseBody
    public ResponseEntity alcometer() {
        if (AlcometerResultTask.currentStatus == StatusType.FREE) {
            AlcometerResultTask.currentStatus = StatusType.REQUEST;

            return ResponseEntity.ok().body("next");
        }

        if (!AlcometerResultTask.currentStatus.skip) {
            return ResponseEntity.ok().body("next");
        }

        if (AlcometerResultTask.currentStatus == StatusType.RESULT) {
            AlcometerResultTask.currentStatus = StatusType.FREE;
            return ResponseEntity.ok().body(AlcometerResultTask.result);
        }

        JSONObject json = new JSONObject();
        json.put("message", "Не удалось получить статус");
        return ResponseEntity.status(500).body(json);
    }

    @PostMapping(value = "/device/alcometer/close")
    @ResponseBody
    public ResponseEntity alcometerClose() {
        AlcometerResultTask task = this.alcometerTaskRunner.getAlcometerResultTask();
        task.close();
        return ResponseEntity.ok().body("");
    }

    @PostMapping("/device/alcometer/status/stop")
    @ResponseBody
    public ResponseEntity stopStatusAlcometrStatusTranslation() {
        for (Session session : AlcometrStatusEndPoint.sessionList) {
            try {
                session.close();
            } catch (IOException e) {
                SdpoLog.error(e);
            }
        }
        return ResponseEntity.status(200).body("ok");
    }

    @PostMapping(value = "/device/alcometer/change-mode")
    @ResponseBody
    public ResponseEntity changeMode(@RequestBody Map<String,  String> json) {
        String modeName = json.get("mode");
        boolean fastMode = modeName.equals("fast");
        boolean slowMode = modeName.equals("slow");
        if (fastMode) Sdpo.settings.systemConfig.set("alcometer_fast", true);
        if (slowMode) Sdpo.settings.systemConfig.set("alcometer_fast", false);
        return ResponseEntity.ok().body("");
    }

}
