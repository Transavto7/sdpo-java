package ru.nozdratenko.sdpo.InspectionManager.Offline.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InspectionNotFound;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InternalServerError;
import ru.nozdratenko.sdpo.InspectionManager.Offline.Action.ChangeStatusUploadInspectionFromLocalStorageAction;
import ru.nozdratenko.sdpo.InspectionManager.Offline.Action.DeleteInspectionFromLocalStorageAction;
import ru.nozdratenko.sdpo.InspectionManager.Offline.ResendStatusEnum;
import ru.nozdratenko.sdpo.InspectionManager.Service.InspectionSenderService;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionLocalStorage;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionLocalStorageRepository;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.net.UnknownHostException;
import java.util.Map;

@RestController
public class InspectionStorageController {
    private final InspectionSenderService inspectionSenderService;

    @Autowired
    public InspectionStorageController(InspectionSenderService inspectionSenderService) {
        this.inspectionSenderService = inspectionSenderService;
    }

    @GetMapping(value = "inspection/local", produces = "application/json; charset=utf-8")
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

    @PostMapping(value="api/inspection/local/send/crm")
    public ResponseEntity sendInspectionToCrm(@RequestBody Map<String, String> json) {
        JSONObject inspection = new JSONObject(json);
        try {
            JSONObject resultJson = this.inspectionSenderService.sendInspectionItem(inspection);
            SdpoLog.info("resultJson: " + resultJson);
            DeleteInspectionFromLocalStorageAction.deleteInspectionByDriverIdAndCreateDate(
                    (String) inspection.get("driver_id"), (String) inspection.get("created_at"));
            return ResponseEntity.status(HttpStatus.OK).body(json.toString());
        }
        catch (InternalServerError e) {
            try {
                ChangeStatusUploadInspectionFromLocalStorageAction.changeStatusByDriverIdAndCreateDate(
                        (String) inspection.get("driver_id"), (String) inspection.get("created_at"), ResendStatusEnum.NO_CONFIRMATION
                );
            } catch (InspectionNotFound ex) {
                SdpoLog.error("!!!!!! INSPECTION NOT FOUND !!!!!!!: \n" + json);
            }
            SdpoLog.error("resultJson - InternalServerError: " + json);
        }
        catch (UnknownHostException e) {
            SdpoLog.error("resultJson - error - Unknown Host: " + e.toString());
        } catch (Exception | ApiException e) {
            SdpoLog.error("resultJson - error: " + e.toString());
        } catch (InspectionNotFound e) {
            return ResponseEntity.status(404).body("error");
        }
        return ResponseEntity.status(403).body("error");
    }

}
