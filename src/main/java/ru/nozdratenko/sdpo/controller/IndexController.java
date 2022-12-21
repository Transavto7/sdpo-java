package ru.nozdratenko.sdpo.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

@Controller
public class IndexController {

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
                    .body(CameraHelper.readVideoByte());
        } catch (IOException e) {
            SdpoLog.error("Error open viode: " + e);
            return ResponseEntity.status(503).body(FileBase.concatPath(FileBase.getMainFolderUrl(), "video.mp4"));
        }
    }

    @PostMapping("api/check")
    public ResponseEntity apiCheck(@RequestBody Map<String, String> json) {
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
                    timestamp = System.currentTimeMillis() - timestamp;
                    return ResponseEntity.ok().body(timestamp);
                }
            }
        } catch (IOException e) {
            SdpoLog.error(e);
        }
        return ResponseEntity.status(403).body("error");
    }

    @GetMapping(value = "api/pv", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity apiGetPoint() {
        try {
            Request request = new Request( "sdpo/pv");
            String response = request.sendGet();
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            SdpoLog.error(e);
        }
        return ResponseEntity.status(403).body("error");
    }

    @GetMapping(value = "api/medics", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity aptMedics() {
        try {
            Request request = new Request( "sdpo/medics");
            String response = request.sendGet();
            return ResponseEntity.ok().body(new JSONObject(response).toMap());
        } catch (IOException e) {
            SdpoLog.error(e);
        }
        return ResponseEntity.status(403).body("error");
    }

    @PostMapping(value = "/exit")
    public void exit() {
        System.exit(0);
    }

    @RequestMapping("/**/{path:[^.]*}")
    public String redirect() {
        return "index.html";
    }
}
