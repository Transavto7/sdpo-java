package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;

import java.io.IOException;

public class InspectionLocalStorageRepository implements InspectionRepositoryInterface {
    @Override
    public JSONArray getInspectionsByDriverHashId(String driverHashId) throws IOException {

        JSONArray response = new JSONArray();
        JSONArray allInspections = this.getInspections();

        for (Object inspection : allInspections) {
            if (inspection instanceof JSONObject) {
                JSONObject json = (JSONObject) inspection;
                if (json.has("driver_id") && json.getString("driver_id").equals(driverHashId)) {
                    response.put(inspection);
                }
            }
        }

        return response;
    }

    public JSONArray getInspections() throws IOException {
        return Sdpo.inspectionStorage.getStore();
    }
}
