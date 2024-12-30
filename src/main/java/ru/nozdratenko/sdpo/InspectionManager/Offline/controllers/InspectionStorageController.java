package ru.nozdratenko.sdpo.InspectionManager.Offline.controllers;

import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionLocalStorage;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionLocalStorageRepository;
import ru.nozdratenko.sdpo.util.SdpoLog;

@RestController
public class InspectionStorageController {

    @GetMapping(value = "api/inspection/local", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity getInspectionFromLocalStorage() {
        try {
            InspectionLocalStorage repository = new InspectionLocalStorageRepository();
            JSONArray inspections = repository.getAll();

            return ResponseEntity.status(HttpStatus.OK).body(inspections.toString());

        } catch (Exception e) {
            SdpoLog.error(e);
        }
        return ResponseEntity.status(403).body("error");
    }

}
