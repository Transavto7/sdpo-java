package ru.nozdratenko.sdpo.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;

import java.util.Map;

@RestController
public class SettingsController {

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity login(@RequestBody Map<String, String> json) {
        if (!json.get("password").equals(Sdpo.mainConfig.getString("password"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password error");
        }

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping(value = "/setting/load")
    @ResponseBody
    public ResponseEntity loadSettings() {
        JSONObject json = new JSONObject();
        json.put("main", Sdpo.mainConfig.getJson());
        return ResponseEntity.status(HttpStatus.OK).body(json.toMap());
    }
}
