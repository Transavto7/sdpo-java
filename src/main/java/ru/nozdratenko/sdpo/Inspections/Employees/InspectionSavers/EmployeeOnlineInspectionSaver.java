package ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeOnlineInspectionSaver implements EmployeeInspectionSaver {

    @Override
    public JSONObject save(Map<String, String> json) throws IOException, ApiException {
        Request response = new Request("sdpo/employees/inspection");
        JSONObject jsonObject = new JSONObject(json);
        SdpoLog.info("Employee inspectionSaveOnline: " + jsonObject.toString(10));
        String result = response.sendPost(jsonObject.toString());
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("Employee inspectionSaveOnline response: " + resultJson);

        return resultJson;
    }
}
