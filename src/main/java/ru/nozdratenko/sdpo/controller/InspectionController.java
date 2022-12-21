package ru.nozdratenko.sdpo.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelper;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@RestController
public class InspectionController {

    @PostMapping("inspection/{id}")
    public ResponseEntity inspectionStart(@PathVariable String id) throws IOException {
        Request response = new Request("check-prop/hash_id/Driver/" + id);
        return ResponseEntity.status(HttpStatus.OK).body(response.sendGet());
    }

    @PostMapping("inspection/save")
    public ResponseEntity inspectionSave(@RequestBody Map<String, String> json) {
        try {
            Request response = new Request("sdpo/anketa");
            JSONObject jsonObject = new JSONObject(json);
            SdpoLog.info(jsonObject.toString(10));
            String result = response.sendPost(jsonObject.toString());

            JSONObject resultJson = new JSONObject(result);
            if (Sdpo.systemConfig.getBoolean("printer_write")) {
                print(resultJson);
            }
            return ResponseEntity.status(HttpStatus.OK).body(resultJson);
        } catch (ApiException e) {
            return ResponseEntity.status(500).body(e.getResponse().toMap());
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error create inspection: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            return ResponseEntity.status(500).body(jsonObject);
        } catch (PrinterException e) {
            return ResponseEntity.status(500).body(e.getResponse());
        }
    }

    public void print(JSONObject json) throws PrintException, IOException, PrinterException, java.awt.print.PrinterException {
        String name = "Неизвестный водитель";
        String result = "ПРОШЕЛ";
        String type = "Предрейсовый/Предсменный";
        String admit = "ДОПУЩЕН";
        String date = "0000-00-00 00:00:00";
        String signature = "неизвестная-подпись";

        if (json.has("driver_fio")) {
            name = "" + json.get("driver_fio");
        }

        if (json.has("admitted")) {
            if (!json.get("admitted").equals("Допущен")) {
                result = "НЕ ПРОШЕЛ";
                admit = "НЕ ДОПУЩЕН";
            }
        }

        if (json.has("created_at")) {
            date = "" + json.get("created_at");
        }

        if (json.has("user_eds")) {
            signature = "" + json.get("user_eds");
        }

        if (json.has("type_view")) {
            type = "" + json.get("type_view");
        }

        BufferedImage image = PrinterHelper.getImage(name, result, type, admit, date, signature);

        PrinterHelper.print(image);
    }

}
