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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OnlineInspectionSaver implements DriverInspectionSaver {
    private final PrinterHelper printerHelper;

    @Override
    public JSONObject save(Map<String, String> json) throws PrintException, IOException, PrinterException, ApiException, java.awt.print.PrinterException {
        Request response = new Request("sdpo/anketa");
        JSONObject jsonObject = new JSONObject(json);
        SdpoLog.info("!!! inspectionSaveOnline: " + jsonObject.toString(10));
        String result = response.sendPost(jsonObject.toString());
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("!!! inspectionSaveOnline response: " + resultJson);

        if (Sdpo.settings.systemConfig.getBoolean("printer_write")) {
            this.printerHelper.print(resultJson);
        }

        if (Sdpo.settings.systemConfig.getBoolean("print_qr_check")) {
            try {
                PDDocument document = PrintService.getVerifiedQrInspectionToPDF(resultJson.getInt("id"));
                String path = PrintService.storeQrToPath(document, Integer.toString(resultJson.getInt("id")));
                resultJson.put("qr_check_path", path);
                this.printerHelper.printFromPDFRotate(document);
                this.printerHelper.setLastQRPath(path);
            } catch (ApiException | IOException error) {
                SdpoLog.warning("Impossible to get qr! Inspection id: " + resultJson.getInt("id"));
                SdpoLog.warning("ERROR: " + error);
            }
        }

        SdpoLog.info("!!! inspectionSaveOnline result: " + resultJson);

        return resultJson;
    }
}
