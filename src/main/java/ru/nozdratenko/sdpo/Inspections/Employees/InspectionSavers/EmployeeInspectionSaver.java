package ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;

import javax.print.PrintException;
import java.io.IOException;
import java.util.Map;

public interface EmployeeInspectionSaver {
    JSONObject save(Map<String, String> json) throws IOException, ApiException;
}
