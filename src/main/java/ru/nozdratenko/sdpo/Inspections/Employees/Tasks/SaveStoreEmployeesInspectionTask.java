package ru.nozdratenko.sdpo.Inspections.Employees.Tasks;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers.EmployeeOnlineInspectionSaver;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.ApiNotFoundException;
import ru.nozdratenko.sdpo.Inspections.Employees.Storages.EmployeeInspectionStorage;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.net.UnknownHostException;

@Component
public class SaveStoreEmployeesInspectionTask extends Thread {
    @Override
    public void run() {
        EmployeeOnlineInspectionSaver inspectionSenderService = SpringContext.getBean(EmployeeOnlineInspectionSaver.class);
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }

            if (!Sdpo.isConnection()) {
                continue;
            }

            EmployeeInspectionStorage inspections = Sdpo.employeeInspectionStorage;

            if (inspections.count() < 1) {
                continue;
            }

            int index = 0;
            while (inspections.count() > 0 && index <= inspections.count() - 1) {

                if (!Sdpo.isConnection()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }

                JSONObject json = inspections.getInspection(index);
                SdpoLog.info("SaveStoreInspectionTask.run: " + json.toString());
                try {
                    JSONObject resultJson = inspectionSenderService.save(json.toMap());
                    SdpoLog.info("resultJson: " + resultJson.toString());
                    inspections.remove(index);
                    index = 0;
                    Sdpo.employeeInspectionStorage.save();
                } catch (ApiNotFoundException e) {
                    break;
                } catch (ApiException e) {
                    SdpoLog.error("SaveStoreEmployeesInspectionTask ApiException: " + e.getMessage());
                    index++;
                } catch (UnknownHostException e) {
                    SdpoLog.error("resultJson - error - Unknown Host: " + e);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                } catch (Exception e) {
                    SdpoLog.error("resultJson - error: " + e);
                    break;
                }
            }
        }
    }
}
