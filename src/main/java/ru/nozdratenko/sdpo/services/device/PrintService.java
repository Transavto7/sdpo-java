package ru.nozdratenko.sdpo.services.device;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintService {

    /**
     * @param idInspection int
     * @return PDDocument
     */
    public static PDDocument getVerifiedQrInspectionToPDF(int idInspection) throws IOException, ApiException {
        Request request = new Request("sdpo/anketa/labeling-qr/"  + idInspection);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", idInspection);
        InputStream resultQr = request.sendPostInputStream(jsonObject.toString());
        return PDDocument.load(resultQr);
    }

    public static String storeQrToPath(PDDocument document, String uuid) throws IOException {
        String path = FileBase.concatPath(
                FileBase.getMainFolderUrl(), "qr_check",
                new SimpleDateFormat("dd-MM-yyyy_k-m-s-S").format(new Date()) + "_" + uuid + ".pdf");
        SdpoLog.info("Save pdf with path: " + path);
        File qrCheck = new File(path);
        qrCheck.getParentFile().mkdirs();
        document.save(qrCheck);
        return path;
    }

}
