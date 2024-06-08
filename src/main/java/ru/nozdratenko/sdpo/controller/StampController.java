package ru.nozdratenko.sdpo.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Map;

@Controller
public class StampController {

    @GetMapping(value = "api/stamps", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity getStamps() {
        try {
            if (Sdpo.isConnection()) {
                Request request = new Request( "sdpo/stamps");
                String response = request.sendGet();
                JSONObject jsonObject = new JSONObject(response);

                return ResponseEntity.ok().body(jsonObject.toMap());
            } else {
                return ResponseEntity.ok().body(Sdpo.serviceDataStorage.getDataFromLocalStorage().toMap());
            }

        } catch (JSONException | IOException | ApiException e) {
            SdpoLog.error("Error get stamp list");
        }
        return ResponseEntity.status(403).body("error");
    }

    @PostMapping(value = "api/stamp/save", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity apiSaveStamp(@RequestBody Map<String, String> json) {
        Sdpo.serviceDataStorage.selectStamp(new JSONObject(json));
//        Sdpo.mainConfig.getJson().put("selected_stamp", json);
//        Sdpo.mainConfig.saveFile();
        return  ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
