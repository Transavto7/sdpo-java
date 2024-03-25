package ru.nozdratenko.sdpo.storage;

import org.json.JSONArray;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionRepositoryInterface;

import java.io.IOException;

public class InspectionDataProvider {

    InspectionRepositoryInterface repository;
    public InspectionDataProvider(InspectionRepositoryInterface repository) {
            this.repository = repository;
    }

    public JSONArray getInspectionsOnDriverHashId (String id) throws IOException {

        return repository.getInspectionsByDriverHashId(id);
    }
}
