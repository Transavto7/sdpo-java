package ru.nozdratenko.sdpo.controller.Device;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.helper.PrinterHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

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
}
