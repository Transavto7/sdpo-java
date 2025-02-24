package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import ru.nozdratenko.sdpo.InspectionManager.Exceptions.InspectionNotFound;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

@Repository
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
                        && object.has("created_at") && object.getString("created_at").equals(createdAt)) {
                    return object;
                }
            }
        }

        throw new InspectionNotFound();
    }

    public int getInspectionsIndexByDriverIdAndCreatedDate(String driverHashId, String createdAt) throws InspectionNotFound {

        JSONArray allInspections = this.getAll();

        for (int i = 0; i < allInspections.length(); i++) {
            JSONObject object = (JSONObject) allInspections.get(i);
            if (object.has("driver_id") && object.getString("driver_id").equals(driverHashId)
                    && object.has("created_at") && object.getString("created_at").equals(createdAt)) {
                return i;
            }
        }
        throw new InspectionNotFound();
    }

    public boolean deleteInspectionByIndex(int index) {
        try {
            JSONArray item = this.getAll();
            item.remove(index);
            Sdpo.inspectionStorage.setStore(item);
            Sdpo.inspectionStorage.save();
            return true;
        } catch (Exception e) {
            SdpoLog.error(e);
            return false;
        }
    }
}
