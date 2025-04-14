package ru.nozdratenko.sdpo.Inspections.Employees.Controllers;

import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.Inspections.Employees.Enums.EmployeeInspectionType;
import ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers.EmployeeInspectionSaver;
import ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers.EmployeeInspectionSaversBuilder;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.event.StopRunProcessesEvent;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.ApiNotFoundException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/employees/inspection")
@AllArgsConstructor
public class EmployeeInspectionController {
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/{id}")
    public ResponseEntity start(@PathVariable String id) throws IOException {
        if (Sdpo.isConnection()) {
            Request response = new Request("sdpo/employees/" + id);
            try {
                String result = response.sendGet();

                return ResponseEntity.status(HttpStatus.OK).body(result);
            } catch (ApiNotFoundException e) {
                JSONObject json = new JSONObject();
                json.put("message", "Данный функционал не поддерживается в вашей версии ЭЖПО");
                return ResponseEntity.status(303).body(json.toMap());
            } catch (ApiException e) {
                SdpoLog.error(e);

                return ResponseEntity.status(303).body(e.getResponse().toMap());
            }
        } else {
            JSONObject employee = Sdpo.employeeStorage.getStore().get(id);

            if (employee != null) {
                EmployeeInspectionType lastInspection  = Sdpo.employeeInspectionStorage.getLastInspectionTypeForEmployee(id);
                JSONArray inspectionType = new JSONArray();

                if (lastInspection != null) {
                    if (lastInspection.equals(EmployeeInspectionType.CLOSE)) {
                        JSONObject response = new JSONObject();
                        response.put("message", "Сотрудник уже имеет запись в этот день");

                        return ResponseEntity.status(303).body(response.toMap());
                    }

                    inspectionType.put(EmployeeInspectionType.CLOSE.toString());
                } else  {
                    inspectionType.put(EmployeeInspectionType.OPEN.toString());
                }

                employee.put("inspection_types", inspectionType);

                return ResponseEntity.status(HttpStatus.OK).body(employee.toMap());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/save")
    public ResponseEntity save(@RequestBody Map<String, Object> json) {
        try {
            EmployeeInspectionSaver inspectionSaver = EmployeeInspectionSaversBuilder.build();
            JSONObject inspection = inspectionSaver.save(json);

            eventPublisher.publishEvent(new StopRunProcessesEvent(this));

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
