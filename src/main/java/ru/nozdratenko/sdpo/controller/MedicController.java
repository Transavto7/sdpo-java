package ru.nozdratenko.sdpo.controller;

import org.json.JSONException;
import org.json.JSONObject;
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
public class MedicController {

    @GetMapping(value = "api/medics", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity aptMedics() {
        try {
            if (Sdpo.isConnection()) {
                Request request = new Request( "sdpo/medics");
                String response = request.sendGet();
                JSONObject jsonObject = new JSONObject(response);

                return ResponseEntity.ok().body(jsonObject.toMap());
            } else {
                return ResponseEntity.ok().body(Sdpo.medicStorage.getDataFromLocalStorage().toMap());
            }

        } catch (JSONException | IOException | ApiException e) {
            SdpoLog.error("Error get medic list");
        }
        return ResponseEntity.status(403).body("error");
    }

    @PostMapping(value = "api/medic", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity apiSaveMedic(@RequestBody Map<String, String> json) {
        Sdpo.mainConfig.getJson().put("selected_medic", json);
        Sdpo.mainConfig.saveFile();
        return ResponseEntity.ok().body("ok");
    }

}
