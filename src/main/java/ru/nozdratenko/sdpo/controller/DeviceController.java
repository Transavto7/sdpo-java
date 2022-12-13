package ru.nozdratenko.sdpo.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.exception.VideoRunException;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.helper.TonometerHelper;

import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class DeviceController {

    @PostMapping(value = "/device/photo")
    @ResponseBody
    public ResponseEntity photo() {
        try {
            BufferedImage image = CameraHelper.makePhoto();
            return ResponseEntity.status(HttpStatus.OK).body(CameraHelper.imageToBase64String(image));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error photo");
        }
    }

    @PostMapping(value = "/device/video")
    @ResponseBody
    public ResponseEntity video() {
        try {
            JSONObject json = new JSONObject();
            int duration = 20;
            int snaps = 15;

            json.put("duration", duration);
            json.put("snaps", snaps);
            CameraHelper.makeVideo(duration, snaps);
            return ResponseEntity.status(HttpStatus.OK).body(json.toMap());
        } catch (VideoRunException e) {
            JSONObject json = new JSONObject();
            json.put("message", "Запись уже запущена.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json.toMap());
        }
    }


    @PostMapping(value = "/device/scan")
    @ResponseBody
    public ResponseEntity scan() {
        JSONObject json = TonometerHelper.scan();
        return ResponseEntity.ok().body(json.toMap());
    }

    @PostMapping(value = "/device/tonometer/{id}")
    @ResponseBody
    public ResponseEntity connect(@PathVariable String id) {
        try {
            String connectedAddress = TonometerHelper.connect(id);
            return ResponseEntity.ok().body(connectedAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error connect");
        }
    }


    @PostMapping(value = "/device/tonometer")
    @ResponseBody
    public ResponseEntity tonometer() {
        JSONObject json = TonometerHelper.scan();
        return ResponseEntity.ok().body("next");
    }
}
