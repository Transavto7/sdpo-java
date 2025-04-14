package ru.nozdratenko.sdpo.Inspections.Drivers.Service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.Inspections.Exceptions.InternalServerError;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class InspectionSenderService {
    private final CameraHelper cameraHelper;

    @Autowired
    public InspectionSenderService(CameraHelper cameraHelper) {
        this.cameraHelper = cameraHelper;
    }

    public JSONObject sendInspectionItem(JSONObject inspection) throws IOException, InternalServerError, ApiException {
        JSONObject resultJson = sendInspection(inspection);
        sendMediaFromInspection(inspection);
        return resultJson;
    }

    private JSONObject sendInspection(JSONObject json) throws IOException, ApiException, InternalServerError {
        Request response = new Request("sdpo/anketa");
        String result = response.sendPost(json.toString());

        if (response.getResponseCode() != 200) {
            throw new InternalServerError("Ответ не обработан. Код ответа: " + response.getResponseCode());
        }
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("1 Saved inspection: " + resultJson);

        return resultJson;
    }

    private void sendMediaFromInspection(JSONObject inspection) {
        try {
            File video = getVideoFileFromInspectionStorage(inspection);
            if (video != null)
                this.cameraHelper.sendVideo(video);
        } catch (FileNotFoundException e) {
            SdpoLog.error("Video " + e.getMessage() + " not found!");
        }

        try {
            File photo = getPhotoFileFromInspectionStorage(inspection);
            if (photo != null)
                this.cameraHelper.sendPhoto(photo);
        } catch (FileNotFoundException e) {
            SdpoLog.error("Image " + e.getMessage() + " not found!");
        }
    }

    private File getVideoFileFromInspectionStorage(JSONObject inspection) throws FileNotFoundException {
        if (inspection.has("video")) {
            String name = sanitizeFileName(inspection.getString("video"));
            return getFileByFolderAndName("video", name);
        }
        return null;
    }

    private File getPhotoFileFromInspectionStorage(JSONObject inspection) throws FileNotFoundException {
        if (inspection.has("photo")) {
            String name = sanitizeFileName(inspection.getString("photo"));
            return getFileByFolderAndName("image", name);
        }
        return null;
    }

    private String sanitizeFileName(String name) {
        String[] split = name.split("/");
        return split[split.length - 1];
    }

    private File getFileByFolderAndName(String folder, String name) throws FileNotFoundException {
        File file = new File(FileBase.concatPath(FileBase.getMainFolderUrl(), folder, name));
        if (file.exists()) {
            return file;
        } else {
            throw new FileNotFoundException(name);
        }
    }
}
