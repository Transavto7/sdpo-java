package ru.nozdratenko.sdpo.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.InspectionManager.Offline.ResendStatusEnum;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.event.StopRunProcessesEvent;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.services.device.PrintService;
import ru.nozdratenko.sdpo.storage.InspectionDataProvider;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionRepositoryFactory;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionRepositoryInterface;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class InspectionController {
    private final ApplicationEventPublisher eventPublisher;
    private final PrinterHelper printerHelper;

    @Autowired
    public InspectionController(ApplicationEventPublisher eventPublisher, PrinterHelper printerHelper) {
        this.eventPublisher = eventPublisher;
        this.printerHelper = printerHelper;
    }

    @PostMapping("inspection/{id}")
    public ResponseEntity inspectionStart(@PathVariable String id) throws IOException {
        String dateEdsEndRaw = Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("validity_eds_end").toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateEdsEnd = LocalDate.parse(dateEdsEndRaw, formatter);

        if (dateEdsEnd.isBefore(LocalDate.now())) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Требуется обновление терминала!");
            return ResponseEntity.status(303).body(map);
        }

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
            JSONObject driver = Sdpo.driverStorage.getStore().get(id);

            if (driver != null) {
                if (driver.has("end_of_ban")) {
                    if (!driver.get("end_of_ban").equals(JSONObject.NULL)) {
                        DateTimeFormatter barFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        DateTimeFormatter viewFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                        String dateEndOfBanRaw = driver.getString("end_of_ban");
                        LocalDateTime dateEndOfBan = LocalDateTime.parse(dateEndOfBanRaw, barFormatter);

                        if (dateEndOfBan.isAfter(LocalDateTime.now())) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("message", "Указанный водитель отстранен до " + dateEndOfBan.format(viewFormatter) + "!");
                            return ResponseEntity.status(303).body(map);
                        }
                    }
                }

                if (driver.has("dismissed")) {
                    if (driver.getBoolean("dismissed")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("message", "Водитель с указанным ID уволен!");
                        return ResponseEntity.status(303).body(map);
                    }
                }

                return ResponseEntity.status(HttpStatus.OK).body(driver.toMap());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("inspection/print")
    public ResponseEntity inspectionPrint() {
        if (this.printerHelper.getLastPrint() == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Нет прошлой печати");
            return ResponseEntity.status(500).body(jsonObject);
        }

        try {
            this.printerHelper.print(this.printerHelper.getLastPrint());
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
        if (this.printerHelper.getLastQRPath() == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Нет прошлой печати");
            return ResponseEntity.status(500).body(jsonObject);
        }
        try {
            File qrCheck = new File(this.printerHelper.getLastQRPath());
            this.printerHelper.printFromPDFRotate(PDDocument.load(qrCheck));
        } catch (IOException error) {
            SdpoLog.warning("Impossible to get qr! File path: " + this.printerHelper.getLastQRPath());
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
        Sdpo.settings.dynamic.set("count_inspections", Sdpo.settings.dynamic.getInt("count_inspections") + 1);
        Sdpo.settings.dynamic.saveFile();
        try {
            ResponseEntity entity;
            if (!Sdpo.isConnection()) {
                entity = this.inspectionSaveOffline(json);
            } else {
                if (Sdpo.settings.systemConfig.getBoolean("manual_mode")) {
                    entity = this.inspectionSavePack(json);
                } else {
                    entity = this.inspectionSaveOnline(json);
                }
            }
            eventPublisher.publishEvent(new StopRunProcessesEvent(this));

            return entity;
        } catch (ApiException e) {
            Sdpo.settings.dynamic.set("count_inspections", Sdpo.settings.dynamic.getInt("count_inspections") - 1);
            Sdpo.settings.dynamic.saveFile();
            return ResponseEntity.status(500).body(e.getResponse().toMap());
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error create inspection: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            Sdpo.settings.dynamic.set("count_inspections", Sdpo.settings.dynamic.getInt("count_inspections") - 1);
            Sdpo.settings.dynamic.saveFile();
            return ResponseEntity.status(500).body(jsonObject);
        } catch (PrinterException e) {
            Sdpo.settings.dynamic.set("count_inspections", Sdpo.settings.dynamic.getInt("count_inspections") - 1);
            Sdpo.settings.dynamic.saveFile();
            return ResponseEntity.status(500).body(e.getResponse());
        }
    }

    public ResponseEntity inspectionSavePack(Map<String, String> json)
            throws IOException, PrintException, PrinterException, ApiException, java.awt.print.PrinterException {
        JSONObject inspection = new JSONObject(json);
        inspection.put("type_anketa", "pak_queue");
        SdpoLog.info("!!! inspectionSavePack.inspection: " + inspection);
        Request response = new Request("sdpo/anketa");
        String result = response.sendPost(inspection.toString());
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("!!! Saved inspectionSavePack response: " + resultJson);

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

        if (Sdpo.settings.systemConfig.getBoolean("printer_write")) {
            this.printerHelper.print(resultJson);
        }
        if (Sdpo.settings.systemConfig.getBoolean("print_qr_check")) {
            try {
                PDDocument document = PrintService.getVerifiedQrInspectionToPDF(resultJson.getInt("id"));
                String path = PrintService.storeQrToPath(document, Integer.toString(resultJson.getInt("id")));
                resultJson.put("qr_check_path", path);
                this.printerHelper.printFromPDFRotate(document);


            } catch (ApiException | IOException error) {
                SdpoLog.warning("Impossible to get qr! Inspection id: " + resultJson.getInt("id"));
                SdpoLog.warning("ERROR: " + error);
            }
        }

        SdpoLog.info("!!! Saved inspectionSavePack result: " + inspection);
        return ResponseEntity.status(HttpStatus.OK).body(resultJson.toMap());
    }

    //todo inspectionManager
    public ResponseEntity inspectionSaveOffline(Map<String, String> json)
            throws PrintException, IOException, PrinterException {
        JSONObject inspection = new JSONObject(json);
        JSONObject driver = Sdpo.driverStorage.getStore().get(inspection.getString("driver_id"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        inspection.put("admitted", "Допущен");
        SdpoLog.info("Start offline saving inspection: " + inspection);
        double temp = 36.6;
        String tonometer = "125/80";
        int pulse = 70;
        double alcometer = 0.0;

        if (Sdpo.settings.mainConfig.getJson().has("selected_medic")) {
            //todo selectMedic(inspection) : inspection
            try {
                inspection.put("user_eds", Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("eds"));
                inspection.put("user_name", Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("name"));
            } catch (JSONException e) {
                SdpoLog.error("Error get medic id");
            }
            try {
                String validity = "Срок действия с " + Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("validity_eds_start") + " по " + Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("validity_eds_end");
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

        if (inspection.has("pulse")) {
            try {
                pulse = Integer.parseInt(inspection.getString("pulse"));
            } catch (JSONException e) {
                SdpoLog.error("Error valueOf double temp people");
            }
        }

        if (temp > 37.0) {
            inspection.put("med_view", "Отстранение");
            inspection.put("admitted", "Не допущен");
        }

        try {
            String[] results = tonometer.split("/");
            int pressureSystolic = Integer.parseInt(results[0]);
            int pressureDiastolic = Integer.parseInt(results[1]);
            if (driver.has("pressure_systolic")) {
                if (
                        driver.getInt("pressure_systolic") < pressureSystolic ||
                                driver.getInt("pressure_diastolic") < pressureDiastolic ||
                                driver.getInt("pulse_upper") < pulse ||
                                driver.getInt("pulse_lower") > pulse
                ) {
                    inspection.put("med_view", "Отстранение");
                    inspection.put("admitted", "Не допущен");
                    driver.put("end_of_ban", currentDateTime.plusMinutes(driver.getInt("time_of_pressure_ban")).format(formatter));
                }
            } else {
                if (pressureSystolic > 150) {
                    inspection.put("med_view", "Отстранение");
                    inspection.put("admitted", "Не допущен");
                }
            }
        } catch (NumberFormatException e) {
            SdpoLog.error("Error value of tonometer result");
        }

        if (alcometer > 0) {
            inspection.put("med_view", "Отстранение");
            inspection.put("admitted", "Не допущен");
            if (driver.has("time_of_alcohol_ban")) {
                driver.put("end_of_ban", currentDateTime.plusMinutes(driver.getInt("time_of_alcohol_ban")).format(formatter));
            }
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        inspection.put("created_at", dateFormat.format(date));
        inspection.put("status_send", ResendStatusEnum.UNSENT);

        Sdpo.inspectionStorage.saveInspection(inspection);
        if (Sdpo.settings.systemConfig.getBoolean("printer_write")) {
            this.printerHelper.print(inspection);
        }
        SdpoLog.info("Offline save: " + inspection);

        return ResponseEntity.status(HttpStatus.OK).body(inspection.toMap());
    }

    public ResponseEntity inspectionSaveOnline(Map<String, String> json)
            throws IOException, ApiException, PrintException, PrinterException, java.awt.print.PrinterException {
        Request response = new Request("sdpo/anketa");
        JSONObject jsonObject = new JSONObject(json);
        SdpoLog.info("!!! inspectionSaveOnline: " + jsonObject.toString(10));
        String result = response.sendPost(jsonObject.toString());
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("!!! inspectionSaveOnline response: " + resultJson);

        if (Sdpo.settings.systemConfig.getBoolean("printer_write")) {
            this.printerHelper.print(resultJson);
        }

        if (Sdpo.settings.systemConfig.getBoolean("print_qr_check")) {
            try {
                PDDocument document = PrintService.getVerifiedQrInspectionToPDF(resultJson.getInt("id"));
                String path = PrintService.storeQrToPath(document, Integer.toString(resultJson.getInt("id")));
                resultJson.put("qr_check_path", path);
                this.printerHelper.printFromPDFRotate(document);
                this.printerHelper.setLastQRPath(path);
            } catch (ApiException | IOException error) {
                SdpoLog.warning("Impossible to get qr! Inspection id: " + resultJson.getInt("id"));
                SdpoLog.warning("ERROR: " + error);
            }
        }

        SdpoLog.info("!!! inspectionSaveOnline result: " + resultJson);

        return ResponseEntity.status(HttpStatus.OK).body(resultJson.toMap());
    }

    @PostMapping("/inspection/feedback")
    public ResponseEntity sendFeedback(@RequestBody Map<String, String> json) throws IOException {
        String id = json.get("id");
        String feedback = json.get("feedback");
        try {
            Request response = new Request("sdpo/forms/" + id + "/feedback");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("feedback", feedback);
            String result = response.sendPost(jsonObject.toString());
            JSONObject resultObject = new JSONObject(result);
            SdpoLog.info("send feedback");
            return ResponseEntity.status(HttpStatus.OK).body(resultObject.toString());
        } catch (ApiException e) {
            SdpoLog.info("error feedback : " + e);
            return ResponseEntity.status(303).body(e.getResponse().toMap());
        }
    }
}
