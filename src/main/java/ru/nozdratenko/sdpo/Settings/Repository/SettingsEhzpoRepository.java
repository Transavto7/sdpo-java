package ru.nozdratenko.sdpo.Settings.Repository;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

@Component
public class SettingsEhzpoRepository {
    public JSONObject getSettings() {
        try {
            Request request = new Request("/sdpo/settings");
            String response = request.sendGet();

            return new JSONObject(response);
        } catch (Exception | ApiException e) {
            SdpoLog.error("Error load settings from EHZPO " + e);
            return null;
        }
    }
}
