package ru.nozdratenko.sdpo.controller.driver;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.Map;

@Controller
public class DriverController {
    private final Sdpo sdpo;

    @Autowired
    public DriverController(Sdpo sdpo) {
        this.sdpo = sdpo;
    }

    @PostMapping("api/driver/phone/save")
    public ResponseEntity apiCheck(@RequestBody Map<String, String> json) {
        try {
            if (!json.keySet().contains("driver_id")) {
                throw new Exception("Not driver");
            }
            if (!json.keySet().contains("phone_number")) {
                throw new Exception("Not number");
            }
            String driverId = json.get("driver_id");
            String phoneNumber = json.get("phone_number");
            Request request = new Request("sdpo/driver/" + driverId + "/phone");
            String response = request.sendPost((new JSONObject()).put("phone", phoneNumber).toString());
            JSONObject jsonObject = new JSONObject(response);
            return ResponseEntity.ok().body(jsonObject.toMap());
        } catch (Exception | ApiException e) {
            SdpoLog.error("Ошибка сохранения номера телефона водителя");
            SdpoLog.error(e);
        }

        return ResponseEntity.status(403).body("error");
    }

}
