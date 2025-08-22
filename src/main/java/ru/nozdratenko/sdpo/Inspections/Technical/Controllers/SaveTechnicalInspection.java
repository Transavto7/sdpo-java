package ru.nozdratenko.sdpo.Inspections.Technical.Controllers;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Inspections.Technical.Services.TechnicalInspectionSenderService;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.Map;

@RestController
@RequestMapping("/technical/inspection")
@AllArgsConstructor
public class SaveTechnicalInspection {
    private final TechnicalInspectionSenderService technicalInspectionSenderService;

    @PostMapping("/save")
    public ResponseEntity save(@RequestBody Map<String, Object> json) {
        try {
            JSONObject inspection = this.technicalInspectionSenderService.save(json);

            return ResponseEntity.status(HttpStatus.OK).body(inspection.toMap());
        } catch (ApiException e) {
            SdpoLog.error("ApiException create inspection: " + e);
            return ResponseEntity.status(500).body(e.getResponse().toMap());
        } catch (Exception e) {
            SdpoLog.error("Error create inspection: " + e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
