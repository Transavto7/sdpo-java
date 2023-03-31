package ru.nozdratenko.sdpo.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.File;

public class SaveStoreInspectionTask extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) { }

            System.out.println("Check queue inspections");

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
                    saveInspection(json);
                    saveMedia(json);
                } catch (JSONException e) {
                    SdpoLog.error("Error inspection store save: " + e.getMessage());
                } finally {
                    inspections.remove(0);
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
                CameraHelper.sendPhoto(file);
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
                CameraHelper.sendVideo(file);
            } else {
                SdpoLog.error("Video " + name + " not found!");
            }
        }
    }

    private void saveInspection(JSONObject json) {
        try {
            Request response = new Request("sdpo/anketa");
            String result = response.sendPost(json.toString());

            JSONObject resultJson = new JSONObject(result);
            SdpoLog.info("Saved inspection: " + resultJson.toString());
        } catch (ApiException | Exception e) {
            e.printStackTrace();
            SdpoLog.error("Error create inspection: " + e.getMessage());
            SdpoLog.error("inspection: " + json.toString());
        }
    }
}
