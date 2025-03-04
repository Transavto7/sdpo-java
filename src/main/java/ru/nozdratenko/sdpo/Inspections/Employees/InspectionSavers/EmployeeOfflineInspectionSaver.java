package ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Inspections.Drivers.Offline.ResendStatusEnum;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeOfflineInspectionSaver implements EmployeeInspectionSaver {

    @Override
    public JSONObject save(Map<String, String> json)
            throws IOException {
        JSONObject inspection = new JSONObject(json);
        JSONObject driver = Sdpo.employeeStorage.getStore().get(inspection.getString("employee_id"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        inspection.put("admitted", "Допущен");
        SdpoLog.info("Start offline saving employee inspection: " + inspection);
        double temp = 36.6;
        String tonometer = "125/80";
        int pulse = 70;
        double alcometer = 0.0;

        if (inspection.has("t_people")) {
            try {
                temp = inspection.getDouble("t_people");
            } catch (JSONException e) {
                SdpoLog.error("Error valueOf double temp people");
            }
        }

        if (inspection.has("alcometer_result")) {
            alcometer = inspection.getDouble("alcometer_result");
        }

        if (inspection.has("tonometer")) {
            tonometer = inspection.getString("tonometer");
        }

        if (inspection.has("pulse")) {
            try {
                pulse = Integer.parseInt(inspection.getString("pulse"));
            } catch (JSONException e) {
                SdpoLog.error("Error valueOf double temp people");
            }
        }

        if (temp > 37.0) {
            inspection.put("med_view", "Отстранение");
            inspection.put("admitted", "Не допущен");
        }

        try {
            String[] results = tonometer.split("/");
            int pressureSystolic = Integer.parseInt(results[0]);
            int pressureDiastolic = Integer.parseInt(results[1]);
            if (driver.has("pressure_systolic")) {
                if (
                        driver.getInt("pressure_systolic") < pressureSystolic ||
                                driver.getInt("pressure_diastolic") < pressureDiastolic ||
                                driver.getInt("pulse_upper") < pulse ||
                                driver.getInt("pulse_lower") > pulse
                ) {
                    inspection.put("med_view", "Отстранение");
                    inspection.put("admitted", "Не допущен");
                    driver.put("end_of_ban", currentDateTime.plusMinutes(driver.getInt("time_of_pressure_ban")).format(formatter));
                }
            } else {
                if (pressureSystolic > 150) {
                    inspection.put("med_view", "Отстранение");
                    inspection.put("admitted", "Не допущен");
                }
            }
        } catch (NumberFormatException e) {
            SdpoLog.error("Error value of tonometer result");
        }

        if (alcometer > 0) {
            inspection.put("med_view", "Отстранение");
            inspection.put("admitted", "Не допущен");
            if (driver.has("time_of_alcohol_ban")) {
                driver.put("end_of_ban", currentDateTime.plusMinutes(driver.getInt("time_of_alcohol_ban")).format(formatter));
            }
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        inspection.put("created_at", dateFormat.format(date));
        inspection.put("status_send", ResendStatusEnum.UNSENT);

        Sdpo.employeeInspectionStorage.putInspection(inspection);
        Sdpo.employeeInspectionStorage.save();

        SdpoLog.info("Employee offline save: " + inspection);

        return inspection;
    }
}
