package ru.nozdratenko.sdpo.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.exception.VideoRunException;
import ru.nozdratenko.sdpo.helper.*;
import ru.nozdratenko.sdpo.task.AlcometerResultTask;
import ru.nozdratenko.sdpo.task.ThermometerResultTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DeviceController {

    @PostMapping(value = "/device/photo")
    @ResponseBody
    public ResponseEntity photo() {
        try {
            BufferedImage image = CameraHelper.makePhoto();
            return ResponseEntity.status(HttpStatus.OK).body(CameraHelper.imageToBase64String(image));
        } catch (IOException e) {
            SdpoLog.error("Error photo: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error photo");
        }
    }

    @PostMapping(value = "/device/video")
    @ResponseBody
    public ResponseEntity video() {
        try {
            JSONObject json = new JSONObject();
            int duration = 20;
            int snaps = 24;

            json.put("duration", duration);
            json.put("snaps", snaps);
            CameraHelper.makeVideo(duration, snaps);
            return ResponseEntity.status(HttpStatus.OK).body(json.toMap());
        } catch (VideoRunException e) {
            SdpoLog.error("Video run exception: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getResponse().toMap());
        }
    }

    @GetMapping(value = "/device/video/size")
    @ResponseBody
    public ResponseEntity videoSize() {
        return ResponseEntity.status(HttpStatus.OK).body(CameraHelper.getSizes().keySet());
    }


    @PostMapping(value = "/device/scan")
    @ResponseBody
    public ResponseEntity scan() {
        JSONObject json = TonometerHelper.scan();
        return ResponseEntity.ok().body(json.toMap());
    }

    @PostMapping(value = "/device/tonometer")
    @ResponseBody
    public ResponseEntity tonometer() throws InterruptedException {
        if (!Sdpo.tonometerResultTask.json.isEmpty()) {
            Map<String, Object> result = new HashMap<>(Sdpo.tonometerResultTask.json.toMap());
            Sdpo.tonometerResultTask.clear();
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/thermometer")
    @ResponseBody
    public ResponseEntity thermometer() {
        ThermometerResultTask task = Sdpo.thermometerResultTask;

        if (task.exist()) {
            double result = task.result;
            task.clear();
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/alcometer")
    @ResponseBody
    public ResponseEntity alcometer() {
        AlcometerResultTask task = Sdpo.alcometerResultTask;
        if (task.currentStatus == AlcometerResultTask.Status.WAIT) {
            return ResponseEntity.ok().body("next");
        }

        if (task.currentStatus == AlcometerResultTask.Status.FREE) {
            task.currentStatus = AlcometerResultTask.Status.WAIT;
            return ResponseEntity.ok().body("next");
        }

        if (task.currentStatus == AlcometerResultTask.Status.ERROR) {
            task.currentStatus = AlcometerResultTask.Status.FREE;
            return ResponseEntity.status(500).body(task.error);
        }

        if (task.currentStatus == AlcometerResultTask.Status.RESULT) {
            task.currentStatus = AlcometerResultTask.Status.FREE;
            return ResponseEntity.ok().body(task.result);
        }


        JSONObject json = new JSONObject();
        json.put("message", "Не удалось получить статус");
        return ResponseEntity.status(500).body(json);
    }

    @PostMapping(value = "/device/printer")
    @ResponseBody
    public ResponseEntity printer() {
        try {
            PrinterHelper.print();
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error printer: " + e);
        } catch (PrinterException e) {
            ResponseEntity.status(500).body(e.getResponse());
            SdpoLog.error("Error printer: " + e);
        }
        return ResponseEntity.ok().body("");
    }
}
