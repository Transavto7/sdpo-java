package ru.nozdratenko.sdpo.controller.devices;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.helper.TonometerHelper;

@RestController
public class ScanController {
    private final TonometerHelper helper;

    @Autowired
    public ScanController(TonometerHelper helper) {
        this.helper = helper;
    }

    @PostMapping(value = "/device/scan")
    @ResponseBody
    public ResponseEntity scan() {
        JSONObject json = this.helper.scan();
        return ResponseEntity.ok().body(json.toMap());
    }
}
