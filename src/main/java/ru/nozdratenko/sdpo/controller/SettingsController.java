package ru.nozdratenko.sdpo.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;

import java.util.Map;

@RestController
public class SettingsController {
    @PostMapping("/setting/load")
    @ResponseBody
    public ResponseEntity loadSettings() {
        JSONObject json = new JSONObject();
        json.put("main", Sdpo.mainConfig.getJson());
        return ResponseEntity.status(HttpStatus.OK).body(json.toMap());
    }

    @PostMapping("/setting/password")
    @ResponseBody
    public ResponseEntity login(@RequestBody Map<String, String> json) {
        if (json.get("password") == null && json.get("password").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password error");
        }

        Sdpo.mainConfig.set("password", json.get("password"));
        Sdpo.mainConfig.saveFile();

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
