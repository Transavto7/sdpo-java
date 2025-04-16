package ru.nozdratenko.sdpo.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.websocket.VideoEndpoint;

import javax.websocket.Session;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Controller
public class IndexController {
    private final CameraHelper cameraHelper;

    @Autowired
    public IndexController(CameraHelper cameraHelper) {
        this.cameraHelper = cameraHelper;
    }

    /**
     * Return main frontend file on start application
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/video")
    public ResponseEntity video() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "video/mp4")
                .body(this.cameraHelper.readVideoByte());
        } catch (IOException e) {
            SdpoLog.error("Error open video: " + e);
            return ResponseEntity.status(503).body(FileBase.concatPath(FileBase.getMainFolderUrl(), "video.mp4"));
        }
    }

    @PostMapping("api/check")
    public ResponseEntity apiCheck(@RequestBody Map<String, String> json) throws InterruptedException {
        try {
            if (json.keySet().contains("address")) {
                String address = json.get("address");
                long timestamp = System.currentTimeMillis();

                if (!address.endsWith("/")) {
                    address += "/";
                }

                Request request = new Request(new URL(address + "sdpo/check"));
                String response = request.sendGet();
                if (response.equals("true")) {
                    Sdpo.setConnection(true);
                    timestamp = System.currentTimeMillis() - timestamp;

                    return ResponseEntity.ok().body(timestamp);
                }
            }
        } catch (UnknownHostException ignored) {
            Thread.sleep(5000);
        } catch (Exception | ApiException e) {
            SdpoLog.error(e);
        }

        Sdpo.setConnection(false);
        return ResponseEntity.status(403).body("error");
    }

    @GetMapping(value = "api/pv", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity apiGetPoint() {
        try {
            Request request = new Request("sdpo/pv");
            String response = request.sendGet();
            if (request.success && response.length() < 500) {
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.status(403).body("error");
            }
        } catch (IOException | ApiException e) {
            SdpoLog.error("Failed to fetch sdpo/pv");
        }
        return ResponseEntity.status(403).body("error");
    }

    @GetMapping(value = "api/verification", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity apiGetVerification() {
        try {
            Request request = new Request("sdpo/terminal/verification");
            String response = request.sendGet();
            if (request.success && response.length() < 500) {
                JSONObject jsonObject = new JSONObject(response);
                try {
                    Sdpo.settings.systemConfig.set("date_check", (String) jsonObject.get("date_check"));
                    Sdpo.settings.systemConfig.set("serial_number", (String) jsonObject.get("serial_number"));
                    if (jsonObject.has("stamp")) {
                        Sdpo.serviceDataStorage.selectStamp(jsonObject.getJSONObject("stamp"));
                    }

                    Sdpo.settings.systemConfig.saveFile();
                } catch (JSONException ignore) {
                }
                return ResponseEntity.ok().body(jsonObject.toMap());
            } else {
                return ResponseEntity.status(403).body("error");
            }
        } catch (IOException | ApiException e) {
            SdpoLog.error(e);
        }
        return ResponseEntity.status(403).body("error");
    }

    @PostMapping(value = "api/logo", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity apiSaveLogo(@RequestBody Map<String, String> json) {
        if (!json.keySet().contains("logo")) {
            return ResponseEntity.status(403).body("logo not found");
        }

        Sdpo.settings.mainConfig.set("logo", json.get("logo")).saveFile();
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/api/photo")
    @ResponseBody
    public ResponseEntity apiSavePhoto(@RequestBody Map<String, String> json) {
        try {
            Request request = new Request("sdpo/driver/" + json.get("driver_id") + "/photo");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("photo", json.get("photo"));
            request.sendPost(jsonObject.toString());
        } catch (IOException e) {
            SdpoLog.error(e);
        } catch (ApiException e) {
            SdpoLog.error(e);
        }
        return ResponseEntity.status(200).body("ok");
    }

    @PostMapping("/api/photo/stop")
    @ResponseBody
    public ResponseEntity apiSavePhoto() {
        for (Session session : VideoEndpoint.sessionList) {
            try {
                session.close();
            } catch (IOException e) {
                SdpoLog.error(e);
            }
        }
        return ResponseEntity.status(200).body("ok");
    }

    @PostMapping(value = "/exit")
    public void exit() {
        SdpoLog.info("Exit router");
        System.exit(0);
    }

    @RequestMapping("/**/{path:[^.]*}")
    public String redirect() {
        return "index.html";
    }
}
