package ru.nozdratenko.sdpo.Settings.Queries;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Settings.Services.AppVersionService;

@Service
@AllArgsConstructor
public class GetAppVersionQuery {
    private final AppVersionService appVersionService;

    public JSONObject handle() {
        JSONObject json = new JSONObject();
        json.put("version", appVersionService.getVersion());
        json.put("name", appVersionService.getName());
        return json;
    }
}
