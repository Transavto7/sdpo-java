package ru.nozdratenko.sdpo.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelper;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.services.device.PrintService;
import ru.nozdratenko.sdpo.storage.InspectionDataProvider;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionRepositoryFactory;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionRepositoryInterface;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
public class InspectionController {

    @PostMapping("inspection/{id}")
    public ResponseEntity inspectionStart(@PathVariable String id) throws IOException {
        if (Sdpo.isConnection()) {
            Request response = new Request("sdpo/driver/" + id);
            try {
                String result = response.sendGet();
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } catch (ApiException e) {
                SdpoLog.error(e);
                return ResponseEntity.status(303).body(e.getResponse().toMap());
            }

        } else {
            String name = Sdpo.driverStorage.getStore().get(id);
            if (name != null) {
                JSONObject response = new JSONObject();
                response.put("hash_id", id);
                response.put("fio", name);
                return ResponseEntity.status(HttpStatus.OK).body(response.toMap());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("inspection/print")
    public ResponseEntity inspectionPrint() {
        if (PrinterHelper.lastPrint == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Нет прошлой печати");
            return ResponseEntity.status(500).body(jsonObject);
        }

        try {
            PrinterHelper.print(PrinterHelper.lastPrint);
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error create inspection: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            return ResponseEntity.status(500).body(jsonObject);
        } catch (PrinterException e) {
            return ResponseEntity.status(500).body(e.getResponse());
        }

        return ResponseEntity.status(200).body("");
    }

    @PostMapping("inspection/print/qr")
    public ResponseEntity inspectionPrintQr() {
        if (PrinterHelper.lastQRPath == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Нет прошлой печати");
            return ResponseEntity.status(500).body(jsonObject);
        }
        try {
            File qrCheck = new File(PrinterHelper.lastQRPath);
            PrinterHelper.printFromPDFRotate(PDDocument.load(qrCheck));
        } catch (IOException error) {
            SdpoLog.warning("Impossible to get qr! File path: " + PrinterHelper.lastQRPath);
            SdpoLog.warning("ERROR: " + error);
        } catch (java.awt.print.PrinterException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(200).body("");
    }

    @PostMapping("api/{id}/inspections")
    public ResponseEntity getInspectionsDriver(@PathVariable String id) throws IOException {
        InspectionRepositoryInterface repository;

        repository = InspectionRepositoryFactory.get();
        JSONArray inspections = (new InspectionDataProvider(repository)).getInspectionsOnDriverHashId(id);

        return ResponseEntity.status(HttpStatus.OK).body(inspections.toString());
    }


    @PostMapping("inspection/save")
    public ResponseEntity inspectionSave(@RequestBody Map<String, String> json) {
        try {
            if (!Sdpo.isConnection()) {
                return this.inspectionSaveOffline(json);
            } else {
                if (Sdpo.systemConfig.getBoolean("manual_mode")) {
                    return this.inspectionSavePack(json);
                } else {
                    return this.inspectionSaveOnline(json);
                }
            }
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

    public ResponseEntity inspectionSavePack(Map<String, String> json)
            throws IOException, PrintException, PrinterException, ApiException, java.awt.print.PrinterException {
        JSONObject inspection = new JSONObject(json);
        inspection.put("type_anketa", "pak_queue");
        SdpoLog.info("!!! inspectionSavePack.inspection: " + inspection.toString());
        Request response = new Request("sdpo/anketa");
        String result = response.sendPost(inspection.toString());
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("2 Saved inspection: " + resultJson.toString());

        if (resultJson.has("id")) {
            int timeout = 20;

            if (resultJson.has("timeout")) {
                timeout = resultJson.getInt("timeout");
            }

            Long start = new Date().getTime();
            boolean timing = true;

            while (true) {
                Long current = new Date().getTime();
                long left = (current - start) / 1000;
                if (left > timeout) {
                    break;
                }

                response = new Request("sdpo/anketa/" + resultJson.getInt("id"));
                result = response.sendGet();
                resultJson = new JSONObject(result);

                if (!resultJson.getString("type_anketa").equals("pak_queue")) {
                    timing = false;
                    break;
                }
            }

            if (timing) {
                String rs = new Request("sdpo/anketa/" + resultJson.getInt("id"))
                        .sendPost();
            }

            response = new Request("sdpo/anketa/" + resultJson.getInt("id"));
            result = response.sendGet();
            resultJson = new JSONObject(result);
        }

        if (Sdpo.systemConfig.getBoolean("printer_write")) {
            PrinterHelper.print(resultJson);
        }
        if (Sdpo.systemConfig.getBoolean("print_qr_check")) {
            try {
                PDDocument document = PrintService.getVerifiedQrInspectionToPDF(resultJson.getInt("id"));
                String path = PrintService.storeQrToPath(document, Integer.toString(resultJson.getInt("id")));
                resultJson.put("qr_check_path", path);
                PrinterHelper.printFromPDFRotate(document);


            } catch (ApiException | IOException error) {
                SdpoLog.warning("Impossible to get qr! Inspection id: " + resultJson.getInt("id"));
                SdpoLog.warning("ERROR: " + error);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(resultJson.toMap());
    }

    public ResponseEntity inspectionSaveOffline(Map<String, String> json)
            throws PrintException, IOException, PrinterException {
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
            try {
                String validity = "Срок действия с " + Sdpo.mainConfig.getJson().getJSONObject("selected_medic").get("validity_eds_start") + " по " + Sdpo.mainConfig.getJson().getJSONObject("selected_medic").get("validity_eds_end");
                inspection.put("validity", validity);
            } catch (JSONException e) {
                SdpoLog.error("Error get medic eds validity");
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

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(date);

        inspection.put("created_at", currentDate);
        Sdpo.inspectionStorage.saveInspection(inspection);
        if (Sdpo.systemConfig.getBoolean("printer_write")) {
            PrinterHelper.print(inspection);
        }
        SdpoLog.info("Offline save: " + inspection.toString());

        return ResponseEntity.status(HttpStatus.OK).body(inspection.toMap());
    }

    public ResponseEntity inspectionSaveOnline(Map<String, String> json)
            throws IOException, ApiException, PrintException, PrinterException, java.awt.print.PrinterException {
        Request response = new Request("sdpo/anketa");
        JSONObject jsonObject = new JSONObject(json);
        SdpoLog.info("!!! inspectionSaveOnline: " + jsonObject.toString(10));
        String result = response.sendPost(jsonObject.toString());

        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("3 Saved inspection: " + resultJson.toString());

        if (Sdpo.systemConfig.getBoolean("printer_write")) {
            PrinterHelper.print(resultJson);
        }

        if (Sdpo.systemConfig.getBoolean("print_qr_check")) {
            try {
                PDDocument document = PrintService.getVerifiedQrInspectionToPDF(resultJson.getInt("id"));
                String path = PrintService.storeQrToPath(document, Integer.toString(resultJson.getInt("id")));
                resultJson.put("qr_check_path", path);
                PrinterHelper.printFromPDFRotate(document);
                PrinterHelper.lastQRPath = path;

            } catch (ApiException | IOException error) {
                SdpoLog.warning("Impossible to get qr! Inspection id: " + resultJson.getInt("id"));
                SdpoLog.warning("ERROR: " + error);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultJson.toMap());
    }

    @PostMapping("/inspection/feedback")
    public ResponseEntity sendFeedback(@RequestBody Map<String, String> json) throws IOException {
        String id = json.get("id");
        String feedback = json.get("feedback");
        try {
            Request response = new Request("sdpo/forms/" + id + "/feedback/");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("feedback", feedback);
            String result = response.sendPost(jsonObject.toString());
            JSONArray resultArray = new JSONArray(result);
            SdpoLog.info("send feedback");
            return ResponseEntity.status(HttpStatus.OK).body(resultArray.toString());
        } catch (ApiException e) {
            SdpoLog.info("error feedback : " + e);
            return ResponseEntity.status(303).body(e.getResponse().toMap());
        }
    }
}
