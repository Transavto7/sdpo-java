package ru.nozdratenko.sdpo.controller.devices;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.helper.CameraHelpers.WindowsCameraHelper;
import ru.nozdratenko.sdpo.network.MultipartUtility;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
public class MediaController {
    private final CameraHelper cameraHelper;

    @Autowired
    public MediaController(CameraHelper cameraHelper) {
        this.cameraHelper = cameraHelper;
    }

    @PostMapping(value = "/device/photo")
    @ResponseBody
    public ResponseEntity photo() {
        try {
            String name = new SimpleDateFormat("dd-MM-yyyy_k-m-s-S").format(new Date());
            SdpoLog.info("Make photo with name: " + name);
            cameraHelper.makePhoto(name);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(MultipartUtility.BACKEND_URL + "/get_file/photo/" + name + ".png");
        } catch (IOException e) {
            SdpoLog.error("Error photo: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error photo");
        }
    }

    @PostMapping(value = "/device/media")
    @ResponseBody
    public ResponseEntity media(@RequestBody Map<String, String> json) {
        SdpoLog.info("Make photo and video");
        JSONObject result = cameraHelper.makePhotoAndVideo(json.get("driver_id"));
        return ResponseEntity.status(HttpStatus.OK).body(result.toMap());
    }

    @PostMapping(value = "/device/media/stop")
    @ResponseBody
    public ResponseEntity mediaStop(@RequestBody Map<String, String> json) {
            SdpoLog.info("Stop photo and video");
        cameraHelper.stopMediaIntoTask();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            /**/
        }
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping(value = "/device/video/test")
    @ResponseBody
    public ResponseEntity videoTest() {
        String name = new SimpleDateFormat("dd-MM-yyyy_k-m-s-S").format(new Date());
        SdpoLog.info("Make photo test with name: " + name);
        cameraHelper.makeVideo(name);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MultipartUtility.BACKEND_URL + "/get_file/video/" + name + ".mp4");
    }

    @GetMapping(value = "/device/video/size")
    @ResponseBody
    public ResponseEntity videoSize() {
        return ResponseEntity.status(HttpStatus.OK).body(cameraHelper.getSizes().keySet());
    }

}
