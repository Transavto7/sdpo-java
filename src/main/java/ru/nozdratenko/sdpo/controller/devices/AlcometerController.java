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
import java.util.HashMap;
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
        HashMap<String, String> map = new HashMap<>();
        map.put("result", null);

        if (AlcometerResultTask.currentStatus == StatusType.FREE) {
            AlcometerResultTask.currentStatus = StatusType.REQUEST;
        }

        if (AlcometerResultTask.currentStatus == StatusType.RESULT) {
            AlcometerResultTask.currentStatus = StatusType.FREE;
            map.put("result", AlcometerResultTask.result);
        }

        map.put("status", AlcometerResultTask.currentStatus.toString());

        return ResponseEntity.ok().body(map);
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
}
