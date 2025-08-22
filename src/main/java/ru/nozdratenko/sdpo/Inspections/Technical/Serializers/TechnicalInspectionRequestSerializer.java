package ru.nozdratenko.sdpo.Inspections.Technical.Serializers;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TechnicalInspectionRequestSerializer {
    public JSONObject serialize(JSONObject inspection) throws Exception {
        JSONObject result = new JSONObject();

        if (!Sdpo.settings.mainConfig.getJson().has("selected_technic")) {
            SdpoLog.error("Не установлен механик для терминала!");

            throw new Exception("Не установлен механик для терминала");
        }

        result.put("user_id", Sdpo.settings.mainConfig.getJson().getJSONObject("selected_technic").get("id"));
        result.put("date", inspection.get("date"));
        result.put("driver_id", inspection.opt("driver_id"));
        result.put("car_id", inspection.opt("car_id"));
        result.put("type_view", inspection.opt("type_view"));
        result.put("odometer", inspection.opt("odometer"));
        result.put("point_reys_control", inspection.opt("point_reys_control"));

        return result;
    }
}
