package ru.nozdratenko.sdpo.task;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InternalServerError;
import ru.nozdratenko.sdpo.InspectionManager.Service.InspectionSenderService;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.net.UnknownHostException;
import java.util.Arrays;

public class SaveStoreInspectionTask extends Thread {

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

                if (!Sdpo.isConnection()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) { }
                    continue;
                }

                JSONObject json = inspections.getJSONObject(0);
                SdpoLog.info("!!! SaveStoreInspectionTask.run: " + json.toString());
                try {
                    JSONObject resultJson = InspectionSenderService.sendInspectionItem(json);
                    SdpoLog.info("resultJson: " + resultJson.toString());
                }
                catch (InternalServerError e) {
                    SdpoLog.error("resultJson - InternalServerError: " + json + " >>> error >>> " + e.toString() + " " + Arrays.toString(e.getStackTrace()));
                    //todo добавить статус записи (несохраненно или не отправленно) и сохранить в стораж
                    break;
                }
                catch (UnknownHostException e) {
                    SdpoLog.error("resultJson - error - Unknown Host: " + e.toString());
                    break;
                } catch (Exception | ApiException e) {
                    SdpoLog.error("resultJson - error: " + e.toString());
                    break;
                } finally {
                    inspections.remove(0);
                    Sdpo.inspectionStorage.save();
                }
            }
        }
    }
}
