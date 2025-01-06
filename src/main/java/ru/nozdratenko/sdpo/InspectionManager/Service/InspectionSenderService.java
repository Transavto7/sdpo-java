package ru.nozdratenko.sdpo.InspectionManager.Service;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InternalServerError;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InspectionSenderService {

    public static JSONObject sendInspectionItem(JSONObject inspection) throws IOException, InternalServerError, ApiException {
        JSONObject resultJson = sendInspection(inspection);
        sendMediaFromInspection(inspection);
        return resultJson;
    }

    private static JSONObject sendInspection(JSONObject json) throws IOException, ApiException, InternalServerError {
        Request response = new Request("sdpo/anketa");
        String result = response.sendPost(json.toString());

        if (response.getResponseCode() != 200) {
            throw new InternalServerError("Ответ не обработан. Код ответа: " + response.getResponseCode());
        }
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("1 Saved inspection: " + resultJson.toString());

        return resultJson;
    }

    private static void sendMediaFromInspection(JSONObject inspection) {
        try {
            File video = getVideoFileFromInspectionStorage(inspection);
            if (video != null)
                CameraHelper.sendVideo(video);
        } catch (FileNotFoundException e) {
            SdpoLog.error("Video " + e.getMessage() + " not found!");
        }

        try {
            File photo = getPhotoFileFromInspectionStorage(inspection);
            if (photo != null)
                CameraHelper.sendPhoto(photo);
        } catch (FileNotFoundException e) {
            SdpoLog.error("Image " + e.getMessage() + " not found!");
        }
    }

    private static File getVideoFileFromInspectionStorage(JSONObject inspection) throws FileNotFoundException {
        if (inspection.has("video")) {
            String name = sanitizeFileName(inspection.getString("video"));
            return getFileByFolderAndName("video", name);
        }
        return null;
    }

    private static File getPhotoFileFromInspectionStorage(JSONObject inspection) throws FileNotFoundException {
        if (inspection.has("photo")) {
            String name = sanitizeFileName(inspection.getString("photo"));
            return getFileByFolderAndName("image", name);
        }
        return null;
    }

    private static String sanitizeFileName(String name) {
        String[] split = name.split("/");
        return split[split.length - 1];
    }

    private static File getFileByFolderAndName(String folder, String name) throws FileNotFoundException {
        File file = new File(FileBase.concatPath(FileBase.getMainFolderUrl(), folder, name));
        if (file.exists()) {
            return file;
        } else {
            throw new FileNotFoundException(name);
        }
    }
}
