package ru.nozdratenko.sdpo.task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.File;
import java.io.IOException;

@Component
public class SaveStoreInspectionTask extends Thread {
    @Autowired
    private CameraHelper cameraHelper;

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) { }

            if (!Sdpo.isConnection()) {
                continue;
            }

            JSONArray inspections = Sdpo.inspectionStorage.getStore();

            if (inspections.length() < 1) {
                continue;
            }

            while (inspections.length() > 0) {
                try {
                    JSONObject json = inspections.getJSONObject(0);
                    SdpoLog.info("!!! SaveStoreInspectionTask.run: " + json.toString());
                    saveInspection(json);
                    saveMedia(json);
                    inspections.remove(0);
                } catch (Exception | ApiException e) {
                    break;
                } finally {
                    Sdpo.inspectionStorage.save();
                }
            }
        }
    }

    private void saveMedia(JSONObject json) {
        if (json.has("photo")) {
            String name = json.getString("photo");
            String[] split = name.split("/");
            name = split[split.length - 1];

            File file = new File(FileBase.concatPath(FileBase.getMainFolderUrl(), "image", name));
            if (file.exists())  {
                cameraHelper.sendPhoto(file);
            } else {
                SdpoLog.error("Image " + name + " not found!");
            }
        }


        if (json.has("video")) {
            String name = json.getString("video");
            String[] split = name.split("/");
            name = split[split.length - 1];

            File file = new File(FileBase.concatPath(FileBase.getMainFolderUrl(), "video", name));
            if (file.exists())  {
                cameraHelper.sendVideo(file);
            } else {
                SdpoLog.error("Video " + name + " not found!");
            }
        }
    }

    private void saveInspection(JSONObject json) throws IOException, ApiException {
        Request response = new Request("sdpo/anketa");
        String result = response.sendPost(json.toString());

        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("1 Saved inspection: " + resultJson.toString());
    }
}
