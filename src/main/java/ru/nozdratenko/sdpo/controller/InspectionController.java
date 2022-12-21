package ru.nozdratenko.sdpo.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Map;

@RestController
public class InspectionController {

    @PostMapping("inspection/{id}")
    public ResponseEntity inspectionStart(@PathVariable String id) throws IOException {
        Request response = new Request("check-prop/hash_id/Driver/" + id);
        return ResponseEntity.status(HttpStatus.OK).body(response.sendGet());
    }

    @PostMapping("inspection/save")
    public ResponseEntity inspectionSave(@RequestBody Map<String, String> json) {
        try {
            Request response = new Request("sdpo/anketa");
            JSONObject jsonObject = new JSONObject(json);
            String result = response.sendPost(jsonObject.toString());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (ApiException e) {
            return ResponseEntity.status(500).body(e.getResponse().toMap());
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error create inspection: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            return ResponseEntity.status(500).body(jsonObject);
        }
    }

}
