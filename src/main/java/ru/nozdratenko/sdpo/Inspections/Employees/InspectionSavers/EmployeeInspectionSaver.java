package ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;

import java.io.IOException;
import java.util.Map;

public interface EmployeeInspectionSaver {
    JSONObject save(Map<String, Object> json) throws IOException, ApiException;
}
