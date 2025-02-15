package ru.nozdratenko.sdpo.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.Map;

@RestController
public class SettingsController {
    @PostMapping("/setting/load")
    @ResponseBody
    public ResponseEntity loadSettings() {
        JSONObject json = new JSONObject();
        json.put("main", Sdpo.mainConfig.getJson());
        json.put("system", Sdpo.systemConfig.getJson());
        return ResponseEntity.status(HttpStatus.OK).body(json.toMap());
    }

    @PostMapping("/setting/password")
    @ResponseBody
    public ResponseEntity savePassword(@RequestBody Map<String, String> json) {
        if (json.get("password") == null && json.get("password").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password error");
        }

        Sdpo.mainConfig.set("password", json.get("password"));
        Sdpo.mainConfig.saveFile();
        SdpoLog.info("Save new password: " + json.get("password"));

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/tonometer")
    @ResponseBody
    public ResponseEntity saveTonometer(@RequestBody Map<String, String> json) {
        if (json.get("address") == null && json.get("address").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("address error");
        }

        Sdpo.mainConfig.set("tonometer_mac", json.get("address").trim());
        Sdpo.mainConfig.saveFile();
        SdpoLog.info("Save new tonometer address: " + json.get("address"));

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/api")
    @ResponseBody
    public ResponseEntity saveApi(@RequestBody Map<String, String> json) {
        if (json.get("address") != null && !json.get("address").isEmpty()) {
            Sdpo.mainConfig.set("url", json.get("address"));
            SdpoLog.info("Save new url: " + json.get("address"));
        }

        if (json.get("token") != null && !json.get("token").isEmpty()) {
            Sdpo.mainConfig.set("token", json.get("token"));
            SdpoLog.info("Save new token: " + json.get("token"));
        }

        Sdpo.mainConfig.saveFile();

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/system")
    @ResponseBody
    public ResponseEntity saveSystem(@RequestBody Map<String, String> json) {
        for (String key : json.keySet()) {
            Sdpo.systemConfig.set(key, json.get(key));
        }

        Sdpo.systemConfig.saveFile();
        try {
            SdpoLog.info("Save new settings system");
            SdpoLog.info("-----[settings]------");
            SdpoLog.info(new JSONObject(json).toString(10));
            SdpoLog.info("-----------");
        } catch (JSONException e) {
            //
        }

        CameraHelper.initDimension();
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/setting/system/auto-send-to-crm/{flag}")
    @ResponseBody
    public ResponseEntity saveAutoSendToCrmFlag(@PathVariable boolean flag) {
        Sdpo.systemConfig.set("auto_send_to_crm", flag);
        Sdpo.systemConfig.saveFile();
        try {
            SdpoLog.info("Save auto_send_to_crm flag in settings system: auto_send_to_crm=" + flag);
        } catch (JSONException e) {
            SdpoLog.error("Exception save flag in setting system auto_send_to_crm with status " + flag);
            SdpoLog.error("Exception: " + e);
            return ResponseEntity.status(500).body("Ошибка сохранения");
        }
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
