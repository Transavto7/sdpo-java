package ru.nozdratenko.sdpo.Inspections.Drivers.Controllers;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Inspections.Drivers.InspectionSavers.DriverInspectionSaver;
import ru.nozdratenko.sdpo.Inspections.Drivers.InspectionSavers.DriverInspectionSaversBuilder;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.event.StopRunProcessesEvent;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class DriverInspectionController {
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

        Request response = new Request("sdpo/driver/" + id);
        try {
            String result = response.sendGet();

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (ApiException e) {
            SdpoLog.error(e);

            return ResponseEntity.status(303).body(e.getResponse().toMap());
        }
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

    @PostMapping("inspection/save")
    public ResponseEntity inspectionSave(@RequestBody Map<String, String> json) {
        try {
            DriverInspectionSaver inspectionSaver = DriverInspectionSaversBuilder.build();
            JSONObject inspection = inspectionSaver.save(json);

            eventPublisher.publishEvent(new StopRunProcessesEvent(this));

            return ResponseEntity.status(HttpStatus.OK).body(inspection.toMap());
        } catch (ApiException e) {
            return ResponseEntity.status(500).body(e.getResponse().toMap());
        } catch (Exception e) {
            SdpoLog.error("Error create inspection: " + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Ошибка запроса");
            return ResponseEntity.status(500).body(jsonObject);
        } catch (PrinterException e) {
            return ResponseEntity.status(500).body(e.getResponse());
        }
    }
}
