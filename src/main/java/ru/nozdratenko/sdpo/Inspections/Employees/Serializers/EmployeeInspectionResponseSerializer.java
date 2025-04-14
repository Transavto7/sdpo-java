package ru.nozdratenko.sdpo.Inspections.Employees.Serializers;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmployeeInspectionResponseSerializer {
    public JSONObject serialize(JSONObject response) {
        SdpoLog.info(response);
        JSONObject result = new JSONObject();

        result.put("proba_alko", response.opt("person_id"));
        result.put("t_people", response.opt("t_people"));
        result.put("type_anketa", response.getInt("type_anketa") == 1 ? "open" : "close");
        result.put("pulse", response.opt("pulse"));
        result.put("alcometer_result", response.opt("alcometer_result"));
        result.put("alcometer_mode", response.opt("alcometer_mode"));
        result.put("test_narko", response.opt("test_narko"));

        if (response.has("pressure_systolic") && response.has("pressure_diastolic")) {
            result.put("tonometer", response.get("pressure_systolic").toString() + "/" + response.get("pressure_diastolic").toString());
        }

        result.put("admitted", response.getBoolean("admitted") ? "Допущен" : "Не допущен");

        return result;
    }
}
