package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class InspectionRemoteRepository implements InspectionRepositoryInterface {

    @Override
    public JSONArray getInspectionsByDriverHashId(String driverHashId) throws IOException {
        Request request = new Request("sdpo/driver/" + driverHashId + "/prints");
        try {
            String response = request.sendGet();

            return new JSONArray(response);
        } catch (ApiException e) {
            SdpoLog.error(e);
            return (new JSONArray()).put(e.getResponse());
        }

    }
}
