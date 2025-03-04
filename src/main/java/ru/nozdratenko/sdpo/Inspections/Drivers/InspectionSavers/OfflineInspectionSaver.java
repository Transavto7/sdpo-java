package ru.nozdratenko.sdpo.Inspections.Drivers.InspectionSavers;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Inspections.Drivers.Offline.ResendStatusEnum;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OfflineInspectionSaver implements DriverInspectionSaver {
    private final PrinterHelper printerHelper;

    @Override
    public JSONObject save(Map<String, String> json)
            throws PrintException, IOException, PrinterException {
        JSONObject inspection = new JSONObject(json);
        JSONObject driver = Sdpo.driverStorage.getStore().get(inspection.getString("driver_id"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        inspection.put("admitted", "Допущен");
        SdpoLog.info("Start offline saving inspection: " + inspection);
        double temp = 36.6;
        String tonometer = "125/80";
        int pulse = 70;
        double alcometer = 0.0;

        if (Sdpo.settings.mainConfig.getJson().has("selected_medic")) {
            try {
                inspection.put("user_eds", Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("eds"));
                inspection.put("user_name", Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("name"));
            } catch (JSONException e) {
                SdpoLog.error("Error get medic id");
            }
            try {
                String validity = "Срок действия с " + Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("validity_eds_start") + " по " + Sdpo.settings.mainConfig.getJson().getJSONObject("selected_medic").get("validity_eds_end");
                inspection.put("validity", validity);
            } catch (JSONException e) {
                SdpoLog.error("Error get medic eds validity");
            }
        }

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

        Sdpo.inspectionStorage.saveInspection(inspection);
        if (Sdpo.settings.systemConfig.getBoolean("printer_write")) {
            this.printerHelper.print(inspection);
        }
        SdpoLog.info("Offline save: " + inspection);

        return inspection;
    }
}
