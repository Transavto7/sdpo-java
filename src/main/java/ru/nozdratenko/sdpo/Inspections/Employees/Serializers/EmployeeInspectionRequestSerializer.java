package ru.nozdratenko.sdpo.Inspections.Employees.Serializers;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmployeeInspectionRequestSerializer {
    public JSONObject serialize(JSONObject inspection) {
        JSONObject result = new JSONObject();

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        result.put("date", currentDateTime.format(formatter));
        result.put("employee_id", inspection.opt("person_id"));
        result.put("t_people", inspection.opt("t_people"));
        result.put("tonometer", inspection.opt("tonometer"));
        result.put("type_anketa", inspection.opt("type_anketa"));
        result.put("pulse", inspection.opt("pulse"));
        result.put("alcometer_result", inspection.opt("alcometer_result"));
        result.put("alcometer_mode", inspection.opt("alcometer_mode"));
        result.put("photo", inspection.opt("photo"));
        result.put("video", inspection.opt("video"));

        result.put("test_narko", inspection.opt("test_narko"));
        result.put("proba_alko", inspection.opt("proba_alko"));

        SdpoLog.info(result);

        return result;
    }
}
