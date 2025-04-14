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
    public JSONObject save(Map<String, Object> json)
            throws IOException {
        JSONObject inspection = new JSONObject(json);
        JSONObject person = Sdpo.employeeStorage.getStore().get(inspection.get("person_id").toString());
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
            SdpoLog.info("Suspension: temp");
        }

        try {
            String[] results = tonometer.split("/");
            int pressureSystolic = Integer.parseInt(results[0]);
            int pressureDiastolic = Integer.parseInt(results[1]);
            if (person.has("pressure_systolic")) {
                if (
                        person.getInt("pressure_systolic") < pressureSystolic ||
                                person.getInt("pressure_diastolic") < pressureDiastolic ||
                                person.getInt("pulse_upper") < pulse ||
                                person.getInt("pulse_lower") > pulse
                ) {
                    inspection.put("med_view", "Отстранение");
                    inspection.put("admitted", "Не допущен");
                    SdpoLog.info("Suspension: pressure_systolic pressureSystolic:"
                            + pressureSystolic + " pressureDiastolic:"
                            + pressureDiastolic + " pulse: " + pulse);
                    SdpoLog.info(person);
                    SdpoLog.info(person.getInt("pulse_upper"));
                    SdpoLog.info(person.getInt("pulse_lower"));
                    SdpoLog.info(person.getInt("pulse_upper") < pulse);
                    SdpoLog.info(person.getInt("pulse_lower") > pulse);
                }
            } else {
                if (pressureSystolic > 150) {
                    inspection.put("med_view", "Отстранение");
                    inspection.put("admitted", "Не допущен");
                    SdpoLog.info("Suspension: pressure_systolic 150");
                }
            }
        } catch (NumberFormatException e) {
            SdpoLog.error("Error value of tonometer result");
        }

        if (alcometer > 0) {
            inspection.put("med_view", "Отстранение");
            inspection.put("admitted", "Не допущен");
            SdpoLog.info("Suspension: alcometer");
            if (person.has("time_of_alcohol_ban")) {
                person.put("end_of_ban", currentDateTime.plusMinutes(person.getInt("time_of_alcohol_ban")).format(formatter));
            }
        }

        inspection.put("created_at", currentDateTime.format(formatter));
        inspection.put("status_send", ResendStatusEnum.UNSENT);

        Sdpo.employeeInspectionStorage.putInspection(inspection);
        Sdpo.employeeInspectionStorage.save();

        SdpoLog.info("Employee offline save: " + inspection);

        return inspection;
    }
}
