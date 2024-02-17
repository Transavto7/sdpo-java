package ru.nozdratenko.sdpo.controller.Device;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.AlcometerResultTask;
import ru.nozdratenko.sdpo.util.StatusType;

@RestController
public class AclometerController {

    @PostMapping(value = "/device/alcometer")
    @ResponseBody
    public ResponseEntity alcometer() {
        AlcometerResultTask task = Sdpo.alcometerResultTask;
        if (task.currentStatus == StatusType.FREE) {
            task.currentStatus = StatusType.REQUEST;
            return ResponseEntity.ok().body("next");
        }

        if (!task.currentStatus.skip) {
            return ResponseEntity.ok().body("next");
        }

        if (task.currentStatus == StatusType.ERROR) {
            return ResponseEntity.status(500).body(task.error.toMap());
        }

        if (task.currentStatus == StatusType.RESULT) {
            task.currentStatus = StatusType.FREE;
            return ResponseEntity.ok().body(task.result);
        }

        JSONObject json = new JSONObject();
        json.put("message", "Не удалось получить статус");
        return ResponseEntity.status(500).body(json);
    }

    @PostMapping(value = "/device/alcometer/close")
    @ResponseBody
    public ResponseEntity alcometerClose() {
        AlcometerResultTask task = Sdpo.alcometerResultTask;
        task.close();
        return ResponseEntity.ok().body("");
    }

}
