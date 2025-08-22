package ru.nozdratenko.sdpo.Inspections.Technical.Services;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.Inspections.Technical.Serializers.TechnicalInspectionRequestSerializer;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TechnicalInspectionSenderService {
    private final TechnicalInspectionRequestSerializer requestSerializer;

    public JSONObject save(Map<String, Object> json) throws IOException, ApiException, Exception {
        Request response = new Request("sdpo/tech/create");
        JSONObject jsonObject = new JSONObject(json);
        SdpoLog.info("Employee inspectionSaveOnline: " + jsonObject.toString(10));
        String result = response.sendPost(requestSerializer.serialize(jsonObject).toString());
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("Employee inspectionSaveOnline response: " + resultJson);

        return resultJson;
    }
}
