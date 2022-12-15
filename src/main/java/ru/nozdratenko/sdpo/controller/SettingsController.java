package ru.nozdratenko.sdpo.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
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

    @PostMapping("/setting/system")
    @ResponseBody
    public ResponseEntity saveSystem(@RequestBody Map<String,  Map<String, String>> json) {
        if (json.get("system") == null && json.get("system").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("system error");
        }

        for (String key : json.get("system").keySet()) {
            Sdpo.systemConfig.set(key, json.get("system").get(key));
        }

        Sdpo.systemConfig.saveFile();
        try {
            SdpoLog.info("Save new settings system");
            SdpoLog.debug("-----[settings]------");
            SdpoLog.debug(new JSONObject(json.get("system")).toString(10));
            SdpoLog.debug("-----------");
        } catch (JSONException e) {
            //
        }

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
