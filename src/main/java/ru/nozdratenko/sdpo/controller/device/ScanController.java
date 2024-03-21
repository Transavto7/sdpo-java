package ru.nozdratenko.sdpo.controller.device;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.helper.TonometerHelper;

@RestController
public class ScanController {

    @PostMapping(value = "/device/scan")
    @ResponseBody
    public ResponseEntity scan() {
        JSONObject json = TonometerHelper.scan();
        return ResponseEntity.ok().body(json.toMap());
    }
}
