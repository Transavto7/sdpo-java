package ru.nozdratenko.sdpo.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.exception.VideoRunException;
import ru.nozdratenko.sdpo.helper.*;
import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.network.MultipartUtility;
import ru.nozdratenko.sdpo.task.AlcometerResultTask;
import ru.nozdratenko.sdpo.task.ThermometerResultTask;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DeviceController {

    @PostMapping(value = "/device/photo")
    @ResponseBody
    public ResponseEntity photo() {
        try {
            String name = new SimpleDateFormat("dd-MM-yyyy_k-m-s").format(new Date());
            CameraHelper.makePhoto(name);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(MultipartUtility.BACKEND_URL + "/get_file/photo/" + name + ".png");
        } catch (IOException e) {
            SdpoLog.error("Error photo: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error photo");
        }
    }

    @PostMapping(value = "/device/media")
    @ResponseBody
    public ResponseEntity media() {
        JSONObject result = CameraHelper.makePhotoAndVideo();
        return ResponseEntity.status(HttpStatus.OK).body(result.toMap());
    }

    @PostMapping(value = "/device/video/test")
    @ResponseBody
    public ResponseEntity videoTest() {
            String name = new SimpleDateFormat("dd-MM-yyyy_k-m-s").format(new Date());
            CameraHelper.makeVideo(name);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(MultipartUtility.BACKEND_URL + "/get_file/video/" + name + ".mp4");
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
        if (Sdpo.tonometerResultTask.currentStatus == StatusType.RESULT) {
            Map<String, Object> result = new HashMap<>(Sdpo.tonometerResultTask.json.toMap());
            Sdpo.tonometerResultTask.currentStatus = StatusType.STOP;
            return ResponseEntity.ok().body(result);
        }

        if (Sdpo.tonometerResultTask.currentStatus == StatusType.FREE) {
            Sdpo.tonometerResultTask.currentStatus = StatusType.REQUEST;
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/tonometer/connect")
    @ResponseBody
    public ResponseEntity tonometerConnect(@RequestBody Map<String, String> json) throws InterruptedException {
        if (json.containsKey("status") && json.get("status").equals("stop")) {
            Sdpo.tonometerConnectTask.currentStatus = StatusType.STOP;
            SdpoLog.info("Stop connecting tonometer");
            return ResponseEntity.ok().body("stop");
        }

        if (Sdpo.tonometerConnectTask.currentStatus == StatusType.RESULT) {
            SdpoLog.info("set connection tonometer");
            Sdpo.tonometerConnectTask.currentStatus = StatusType.WAIT;
            return ResponseEntity.ok().body("set");
        }

        if (Sdpo.tonometerConnectTask.currentStatus == StatusType.FREE) {
            SdpoLog.info("Start connecting tonometer");
            Sdpo.tonometerConnectTask.currentStatus = StatusType.WAIT;
        }

        return ResponseEntity.ok().body("next");
    }

    @PostMapping(value = "/device/tonometer/disable")
    @ResponseBody
    public ResponseEntity tonometerDisable() throws InterruptedException {
        Sdpo.tonometerResultTask.currentStatus = StatusType.STOP;
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
        if (task.currentStatus == StatusType.FREE) {
            task.currentStatus = StatusType.REQUEST;
            return ResponseEntity.ok().body("next");
        }

        if (!task.currentStatus.skip) {
            return ResponseEntity.ok().body("next");
        }

        if (task.currentStatus == StatusType.ERROR) {
            SdpoLog.error(task.error.toString());
            task.currentStatus = StatusType.FREE;
            return ResponseEntity.status(500).body(task.error.toMap());
        }

        if (task.currentStatus == StatusType.RESULT) {
            task.currentStatus = StatusType.FREE;
            return ResponseEntity.ok().body(task.result);
        }

        JSONObject json = new JSONObject();
        json.put("message", "Не удалось получить статус");
        return ResponseEntity.status(500).body(json);
    }

    @PostMapping(value = "/device/alcometer/close")
    @ResponseBody
    public ResponseEntity alcometerClose() {
        AlcometerResultTask task = Sdpo.alcometerResultTask;
        task.close();
        return ResponseEntity.ok().body("");
    }

    @PostMapping(value = "/device/printer")
    @ResponseBody
    public ResponseEntity printer() {
        try {
            PrinterHelper.print("Тестовый Тест Тестович",
                    "прошел",
                    "Предрейсовый/Предсменный",
                    "допущен",
                    "23-10-2022 06:00:00",
                    "test-cat-test",
                    "Котов Кот Котыч");
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error printer: " + e);
        }
        return ResponseEntity.ok().body("");
    }
}
