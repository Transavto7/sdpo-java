package ru.nozdratenko.sdpo.controller;

import jssc.SerialPortException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.exception.VideoRunException;
import ru.nozdratenko.sdpo.helper.*;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
        Sdpo.tonometerResultTask.clear();
        int time = 0;

        while (true) {
            if (!Sdpo.tonometerResultTask.json.isEmpty()) {
                return ResponseEntity.ok().body(Sdpo.tonometerResultTask.json.toMap());
            }
            if (time++ > 180) {
                return ResponseEntity.status(500).body("timeout");
            }

            Thread.sleep(1000);
        }
    }

    @PostMapping(value = "/device/thermometer")
    @ResponseBody
    public ResponseEntity thermometer() {
        try {
            double result = ThermometerHelper.getTemp();
            return ResponseEntity.ok().body(result);
        } catch (SerialPortException e) {
            JSONObject json = new JSONObject();
            json.put("message", "Ошибка подключения термометра");
            SdpoLog.error("Thermometer exception: " + e);
            return ResponseEntity.status(500).body(json.toMap());
        }
    }

    @PostMapping(value = "/device/alcometer")
    @ResponseBody
    public ResponseEntity alcometer() {
        try {
            String result = AlcometerHelper.getResult();
            return ResponseEntity.ok().body(result);
        } catch (SerialPortException | UnsupportedEncodingException e) {
            e.printStackTrace();
            JSONObject json = new JSONObject();
            json.put("message", "Ошибка подключения алкометра");
            SdpoLog.error("Alcometer error: " + e);
            return ResponseEntity.status(500).body(json.toMap());
        } catch (AlcometerException e) {
            SdpoLog.error("Alcometer exception: " + e);
            return ResponseEntity.status(500).body(e.getResponse().toMap());
        }
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
