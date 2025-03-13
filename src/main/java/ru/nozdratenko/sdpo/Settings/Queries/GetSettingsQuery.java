package ru.nozdratenko.sdpo.Settings.Queries;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Sdpo;

@Service
public class GetSettingsQuery {
    public JSONObject handle() {
        JSONObject json = new JSONObject();
        json.put("main", Sdpo.settings.mainConfig.getJson());
        json.put("connection", Sdpo.connectionConfig.getJson());
        json.put("system", Sdpo.settings.systemConfig.getJson());
        json.put("dynamic", Sdpo.settings.dynamicConfig.getJson());
        json.put("temporary", Sdpo.settings.temporaryConfig.getJson());

        return json;
    }
}
