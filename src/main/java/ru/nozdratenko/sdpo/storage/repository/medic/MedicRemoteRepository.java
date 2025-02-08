package ru.nozdratenko.sdpo.storage.repository.medic;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.storage.repository.GetRequest;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class MedicRemoteRepository implements GetRequest {

    @Override
    public JSONObject get() {
        return this.send("sdpo/medics");
    }

    public JSONObject send(String url) {
        try {
            Request request = new Request(url);
            String response = request.sendGet();
            return new JSONObject(response);
        } catch (IOException | ApiException e) {
            SdpoLog.error("Error load MEDIC list");
            throw new RuntimeException(e);
        }
    }
}
