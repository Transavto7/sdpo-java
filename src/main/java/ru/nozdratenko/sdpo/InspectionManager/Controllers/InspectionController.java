package ru.nozdratenko.sdpo.InspectionManager.Controllers;

import lombok.AllArgsConstructor;
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
import ru.nozdratenko.sdpo.InspectionManager.InspectionSavers.InspectionSaver;
import ru.nozdratenko.sdpo.InspectionManager.InspectionSavers.InspectionSaversBuilder;
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
@AllArgsConstructor
public class InspectionController {
    private final ApplicationEventPublisher eventPublisher;
    private final PrinterHelper printerHelper;

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
        Sdpo.settings.systemConfig.set("count_inspections", Sdpo.settings.systemConfig.getInt("count_inspections") + 1);
        Sdpo.settings.systemConfig.saveFile();
        try {
            InspectionSaver inspectionSaver = InspectionSaversBuilder.build();
            JSONObject inspection =  inspectionSaver.save(json);

            eventPublisher.publishEvent(new StopRunProcessesEvent(this));

            return ResponseEntity.status(HttpStatus.OK).body(inspection.toMap());
        } catch (ApiException e) {
            Sdpo.settings.systemConfig.set("count_inspections", Sdpo.settings.systemConfig.getInt("count_inspections") - 1);
            Sdpo.settings.systemConfig.saveFile();
            return ResponseEntity.status(500).body(e.getResponse().toMap());
        } catch (Exception e) {
            SdpoLog.error("Error create inspection: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            Sdpo.settings.systemConfig.set("count_inspections", Sdpo.settings.systemConfig.getInt("count_inspections") - 1);
            Sdpo.settings.systemConfig.saveFile();
            return ResponseEntity.status(500).body(jsonObject);
        } catch (PrinterException e) {
            Sdpo.settings.systemConfig.set("count_inspections", Sdpo.settings.systemConfig.getInt("count_inspections") - 1);
            Sdpo.settings.systemConfig.saveFile();
            return ResponseEntity.status(500).body(e.getResponse());
        }
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
