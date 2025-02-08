package ru.nozdratenko.sdpo.controller.car;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

@Controller
public class CarController {

    @GetMapping("api/car/{id}")
    public ResponseEntity getCar(@PathVariable String id) throws IOException {
        if (Sdpo.isConnection()) {
            Request response = new Request("sdpo/car/" + id);
            try {
                String result = response.sendGet();
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } catch (ApiException e) {
                SdpoLog.error(e);
                return ResponseEntity.status(303).body(e.getResponse().toMap());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Отсутствует подключение к сети");
        return ResponseEntity.status(500).body(jsonObject);
    }

}
