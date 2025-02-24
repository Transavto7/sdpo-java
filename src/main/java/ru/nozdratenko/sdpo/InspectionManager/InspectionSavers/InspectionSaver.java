package ru.nozdratenko.sdpo.InspectionManager.InspectionSavers;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;

import javax.print.PrintException;
import java.io.IOException;
import java.util.Map;

public interface InspectionSaver {
    JSONObject save(Map<String, String> json) throws PrintException, IOException, PrinterException, ApiException, java.awt.print.PrinterException;
}
