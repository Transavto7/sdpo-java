package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.InspectionManager.Exceptions.InspectionNotFound;

public interface InspectionLocalStorage {

    public JSONArray getAll();
    public JSONObject getInspectionsByDriverIdAndCreatedDate(String driverHashId, String createdAt) throws InspectionNotFound;

}
