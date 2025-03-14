package ru.nozdratenko.sdpo.Settings.Http;

import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.Settings.Queries.GetSettingsQuery;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.Map;

@RestController
@AllArgsConstructor
public class SettingsController {
    private final CameraHelper cameraHelper;
    private final GetSettingsQuery getSettingsQuery;

    @PostMapping("/setting/load")
    @ResponseBody
    public ResponseEntity loadSettings() {
        return ResponseEntity.status(HttpStatus.OK).body(getSettingsQuery.handle().toMap());
    }

    @PostMapping("/setting/password")
    @ResponseBody
    public ResponseEntity savePassword(@RequestBody Map<String, String> json) {
        if (json.get("password") == null && json.get("password").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password error");
        }

        Sdpo.settings.mainConfig.set("password", json.get("password"));
        Sdpo.settings.mainConfig.saveFile();
        SdpoLog.info("Save new password: " + json.get("password"));

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/tonometer")
    @ResponseBody
    public ResponseEntity saveTonometer(@RequestBody Map<String, String> json) {
        if (json.get("address") == null && json.get("address").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("address error");
        }

        Sdpo.settings.mainConfig.set("tonometer_mac", json.get("address").trim());
        Sdpo.settings.mainConfig.saveFile();
        SdpoLog.info("Save new tonometer address: " + json.get("address"));

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/api")
    @ResponseBody
    public ResponseEntity saveApi(@RequestBody Map<String, String> json) {
        if (json.get("address") != null && !json.get("address").isEmpty()) {
            Sdpo.connectionConfig.set("url", json.get("address"));
            SdpoLog.info("Save new url: " + json.get("address"));
        }

        if (json.get("token") != null && !json.get("token").isEmpty()) {
            Sdpo.connectionConfig.set("token", json.get("token"));
            SdpoLog.info("Save new token: " + json.get("token"));
        }

        Sdpo.connectionConfig.saveFile();

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/system")
    @ResponseBody
    public ResponseEntity saveSystem(@RequestBody Map<String, Object> json) {
        for (String key : json.keySet()) {
            Sdpo.settings.systemConfig.set(key, json.get(key));
        }

        Sdpo.settings.systemConfig.saveFile();
        try {
            SdpoLog.info("Save new settings system");
            SdpoLog.info("-----[settings]------");
            SdpoLog.info(new JSONObject(json).toString(10));
            SdpoLog.info("-----------");
        } catch (JSONException e) {
            //
        }

        this.cameraHelper.initDimension();
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/system/auto-send-to-crm/{flag}")
    @ResponseBody
    public ResponseEntity saveAutoSendToCrmFlag(@PathVariable boolean flag) {
        Sdpo.settings.systemConfig.set("auto_send_to_crm", flag);
        Sdpo.settings.systemConfig.saveFile();
        try {
            SdpoLog.info("Save auto_send_to_crm flag in settings system: auto_send_to_crm=" + flag);
        } catch (JSONException e) {
            SdpoLog.error("Exception save flag in setting system auto_send_to_crm with status " + flag);
            SdpoLog.error("Exception: " + e);
            return ResponseEntity.status(500).body("Ошибка сохранения");
        }
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/temporary/cursor")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void setCursor(@RequestBody Map<String, Object> json) {
        Sdpo.settings.systemConfig.set("cursor", json.get("cursor"));
        Sdpo.settings.systemConfig.saveFile();
    }
}
