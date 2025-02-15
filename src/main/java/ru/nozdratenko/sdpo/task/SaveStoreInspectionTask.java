package ru.nozdratenko.sdpo.task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InspectionNotFound;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InternalServerError;
import ru.nozdratenko.sdpo.InspectionManager.Offline.Action.ChangeStatusUploadInspectionFromLocalStorageAction;
import ru.nozdratenko.sdpo.InspectionManager.Offline.ResendStatusEnum;
import ru.nozdratenko.sdpo.InspectionManager.Service.InspectionSenderService;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.File;
import java.net.UnknownHostException;

@Component
public class SaveStoreInspectionTask extends Thread {
    @Override
    public void run() {
        InspectionSenderService inspectionSenderService = SpringContext.getBean(InspectionSenderService.class);
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {}

            if (!Sdpo.isConnection()) {
                continue;
            }

            if (!Sdpo.settings.systemConfig.getBoolean("auto_send_to_crm")) {
                try {
                    Thread.sleep(10000);
                    continue;
                } catch (InterruptedException ignored) { }
            }

            JSONArray inspections = Sdpo.inspectionStorage.getStore();

            if (inspections.length() < 1) {
                continue;
            }
            int index = 0;
            while (inspections.length() > 0 && index <= inspections.length()) {

                if (!Sdpo.isConnection()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) { }
                    continue;
                }

                JSONObject json = inspections.getJSONObject(index);
                SdpoLog.info("!!! SaveStoreInspectionTask.run: " + json.toString());
                try {
                    JSONObject resultJson = inspectionSenderService.sendInspectionItem(json);
                    SdpoLog.info("resultJson: " + resultJson.toString());
                    inspections.remove(index);
                    index = 0;
                    Sdpo.inspectionStorage.save();
                }
                catch (InternalServerError e) {
                    try {
                        ChangeStatusUploadInspectionFromLocalStorageAction.changeStatusByDriverIdAndCreateDate(
                                (String) json.get("driver_id"), (String) json.get("created_at"), ResendStatusEnum.NO_CONFIRMATION
                        );
                    } catch (InspectionNotFound ex) {
                        SdpoLog.error("!!!!!! INSPECTION NOT FOUND !!!!!!!: \n" + json);
                    }
                    SdpoLog.error("resultJson - InternalServerError: " + json);
                    index++;
                }
                catch (UnknownHostException e) {
                    SdpoLog.error("resultJson - error - Unknown Host: " + e.toString());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                } catch (Exception | ApiException e) {
                    SdpoLog.error("resultJson - error: " + e.toString());
                    break;
                }
            }
        }
    }
}
