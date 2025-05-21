package ru.nozdratenko.sdpo.Inspections.Drivers.InspectionSavers;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;
import ru.nozdratenko.sdpo.services.device.PrintService;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PackInspectionSaver implements DriverInspectionSaver {
    private final PrinterHelper printerHelper;

    @Override
    public JSONObject save(Map<String, String> json) throws PrintException, IOException, PrinterException, ApiException, java.awt.print.PrinterException {
        JSONObject inspection = new JSONObject(json);
        inspection.put("type_anketa", "pak_queue");
        SdpoLog.info("!!! inspectionSavePack.inspection: " + inspection);
        Request response = new Request("sdpo/anketa");
        String result = response.sendPost(inspection.toString());
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("!!! Saved inspectionSavePack response: " + resultJson);

        if (resultJson.has("id")) {
            do {
                response = new Request("sdpo/anketa/" + resultJson.getInt("id"));
                result = response.sendGet();
                resultJson = new JSONObject(result);

            } while (resultJson.getString("type_anketa").equals("pak_queue"));
        }

        if (Sdpo.settings.systemConfig.getBoolean("printer_write")) {
            this.printerHelper.print(resultJson);
        }

        if (Sdpo.settings.systemConfig.getBoolean("print_qr_check")) {
            try {
                PDDocument document = PrintService.getVerifiedQrInspectionToPDF(resultJson.getInt("id"));
                String path = PrintService.storeQrToPath(document, Integer.toString(resultJson.getInt("id")));
                resultJson.put("qr_check_path", path);
                this.printerHelper.printFromPDFRotate(document);
            } catch (ApiException | IOException error) {
                SdpoLog.warning("Impossible to get qr! Inspection id: " + resultJson.getInt("id"));
                SdpoLog.warning("ERROR: " + error);
            }
        }

        SdpoLog.info("!!! Saved inspectionSavePack result: " + inspection);

        return resultJson;
    }
}
