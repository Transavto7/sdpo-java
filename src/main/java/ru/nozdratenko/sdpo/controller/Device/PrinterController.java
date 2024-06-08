package ru.nozdratenko.sdpo.controller.Device;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.io.IOException;
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
        try {
            PrinterHelper.print(inspection);
        } catch (PrinterException e) {
            return ResponseEntity.status(500).body(e.getResponse());
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error create inspection: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            return ResponseEntity.status(500).body(jsonObject);
        }

        return ResponseEntity.ok().body("");
    }

}
