package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;

import java.io.IOException;

public interface InspectionRepositoryInterface {

    public JSONArray getInspectionsByDriverHashId(String driverHashId) throws IOException;
}
