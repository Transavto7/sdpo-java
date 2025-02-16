package ru.nozdratenko.sdpo.storage.repository.stamp;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.storage.repository.GetRequest;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

@Repository
public class StampRemoteRepository implements GetRequest {

    @Override
    public JSONObject get() {
        return this.send("sdpo/stamp");
    }

    public JSONObject all() {
        return this.send("sdpo/stamps");
    }

    public JSONObject send(String url) {
        try {
            Request request = new Request(url);
            String response = request.sendGet();
            return new JSONObject(response);
        } catch (IOException | ApiException e) {
            SdpoLog.error("Error load STAMP list");
            throw new RuntimeException(e);
        }
    }
}
