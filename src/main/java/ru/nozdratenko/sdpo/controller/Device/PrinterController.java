package ru.nozdratenko.sdpo.controller.Device;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
public class PrinterController {

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
                    "Котов Кот Котыч",
                    "С 1.01.2000 по 1.01.2077");
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error printer: " + e);
        }
        return ResponseEntity.ok().body("");
    }


    @PostMapping(value = "/device/printer/inspection")
    @ResponseBody
    public ResponseEntity printer(@RequestBody Map<String, String> json) throws PrintException, IOException, PrinterException {
        JSONObject inspection = new JSONObject(json);
        inspection.put("admitted", "Допущен");

        double temp = 36.6;
        String tonometer = "125/80";
        double alcometer = 0.0;

        if (Sdpo.mainConfig.getJson().has("selected_medic")) {
            try {
                inspection.put("user_eds", Sdpo.mainConfig.getJson().getJSONObject("selected_medic").get("eds"));
                inspection.put("user_name", Sdpo.mainConfig.getJson().getJSONObject("selected_medic").get("name"));
            } catch (JSONException e) {
                SdpoLog.error("Error get medic id");
            }
        }

        if (inspection.has("t_people")) {
            try {
                temp = inspection.getDouble("t_people");
            } catch (JSONException e) {
                SdpoLog.error("Error valueOf double temp people");
            }
        }

        if (inspection.has("alcometer_result")) {
            alcometer = inspection.getDouble("alcometer_result");
        }

        if (inspection.has("tonometer")) {
            tonometer = inspection.getString("tonometer");
        }

        if (temp > 37.0) {
            inspection.put("med_view", "Отстранение");
            inspection.put("admitted", "Не допущен");
        }

        if (alcometer > 0) {
            inspection.put("med_view", "Отстранение");
            inspection.put("admitted", "Не допущен");
        }

        try {
            int tonRs = Integer.valueOf(tonometer.split("/")[0]);
            if (tonRs > 150) {
                inspection.put("med_view", "Отстранение");
                inspection.put("admitted", "Не допущен");
            }
        } catch (NumberFormatException e) {
            SdpoLog.error("Error value of tonometer result");
        }

            PrinterHelper.print(inspection);

        return ResponseEntity.ok().body("");
    }

}
