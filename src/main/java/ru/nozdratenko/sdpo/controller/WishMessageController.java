package ru.nozdratenko.sdpo.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

@Controller
public class WishMessageController {

    @GetMapping(value = "api/wish-message", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity getStamps() {
        try {
            if (Sdpo.isConnection()) {
                Request request = new Request( "sdpo/wish-message");
                String response = request.sendGet();
                JSONObject jsonObject = new JSONObject(response);

                return ResponseEntity.ok().body(jsonObject.toMap());
            } else {
                return ResponseEntity.ok().body("");
            }

        } catch (JSONException | IOException | ApiException e) {
            SdpoLog.error("Error get wish message");
            SdpoLog.error(e);
        }
        return ResponseEntity.status(403).body("error");
    }
}
