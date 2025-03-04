package ru.nozdratenko.sdpo.Inspections.Drivers.InspectionSavers;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;

import javax.print.PrintException;
import java.io.IOException;
import java.util.Map;

public interface DriverInspectionSaver {
    JSONObject save(Map<String, String> json) throws PrintException, IOException, PrinterException, ApiException, java.awt.print.PrinterException;
}
