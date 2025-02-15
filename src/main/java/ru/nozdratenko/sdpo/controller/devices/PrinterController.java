package ru.nozdratenko.sdpo.controller.devices;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.services.device.PrintService;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
public class PrinterController {
    private final PrinterHelper printerHelper;

    @Autowired
    public PrinterController(PrinterHelper printerHelper) {
        this.printerHelper = printerHelper;
    }

    @PostMapping(value = "/device/printer")
    @ResponseBody
    public ResponseEntity printer() {
        try {
            this.printerHelper.print("Тестовый Тест Тестович",
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


    @PostMapping(value = "/device/printer/inspection/verified/qr")
    @ResponseBody
    public ResponseEntity printVerifyInspectionQR(@RequestBody Map<String, String> json) {
        if (Sdpo.settings.systemConfig.getBoolean("print_qr_check")) {
            try {
                PDDocument document = PrintService.getVerifiedQrInspectionToPDF(Integer.parseInt(json.get("id")));
                this.printerHelper.printFromPDFRotate(document);

            } catch (ApiException | IOException error) {
                SdpoLog.warning("Impossible to get qr! Inspection id: " + json.get("id"));
                SdpoLog.warning("ERROR: " + error);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", "Ошибка запроса");
            } catch (java.awt.print.PrinterException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping(value = "/device/printer/inspection")
    @ResponseBody
    public ResponseEntity printer(@RequestBody Map<String, String> json) throws PrintException, IOException, PrinterException {
        JSONObject inspection = new JSONObject(json);
        try {
            this.printerHelper.print(inspection);
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

    @PostMapping(value = "/device/printer/qr")
    @ResponseBody
    public ResponseEntity printQr(@RequestBody Map<String, String> json) {
        try {
            Request request = new Request("sdpo/get-sticker");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", json.get("type"));
            jsonObject.put("id", json.get("driver"));

            InputStream result =  request.sendPostInputStream(jsonObject.toString());
            SdpoLog.info(result);

            PDDocument document = PDDocument.load(result);
            this.printerHelper.printFromPDF(document);
        } catch (IOException | ApiException e) {
                SdpoLog.error(e);
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error printer: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            return ResponseEntity.status(500).body(jsonObject);
        }

        return ResponseEntity.ok().body("");
    }


}
