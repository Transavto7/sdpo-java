package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InspectionNotFound;
import ru.nozdratenko.sdpo.Sdpo;

import java.io.IOException;

public class InspectionLocalStorageRepository implements InspectionRepositoryInterface, InspectionLocalStorage {
    @Override
    public JSONArray getInspectionsByDriverHashId(String driverHashId) throws IOException {

        JSONArray response = new JSONArray();
        JSONArray allInspections = this.getAll();

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

    @Override
    public JSONArray getAll() {
        return Sdpo.inspectionStorage.getStore();
    }

    public JSONObject getInspectionsByDriverIdAndCreatedDate(String driverHashId, String createdAt) throws InspectionNotFound {

        JSONArray allInspections = this.getAll();

        for (Object inspection : allInspections) {
            if (inspection instanceof JSONObject) {
                JSONObject object = (JSONObject) inspection;
                if (object.has("driver_id") && object.getString("driver_id").equals(driverHashId)
                && object.has("created_at") && object.getString("created_at").equals(createdAt) ) {
                    return object;
                }
            }
        }

        throw new InspectionNotFound();
    }
}
