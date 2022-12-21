package ru.nozdratenko.sdpo.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/**/{path:[^.]*}")
    public String redirect() {
        return "index.html";
    }
}
