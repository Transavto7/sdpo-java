package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;
import org.json.JSONObject;

public interface InspectionRepositoryInterface {
    public JSONArray getInspections(String driverHashId);
}
