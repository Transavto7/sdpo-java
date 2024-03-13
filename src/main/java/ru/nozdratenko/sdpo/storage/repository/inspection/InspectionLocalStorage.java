package ru.nozdratenko.sdpo.storage.repository.inspection;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class InspectionLocalStorage extends FileBase implements InspectionRepositoryInterface {

    public JSONArray store = new JSONArray();
    public JSONObject storeObjects = new JSONObject();


    public InspectionLocalStorage() {
        this("storage/inspections.json");

    }

    public InspectionLocalStorage(String path) {
        super(path);

        try {
            String str = read();
            if (!str.isEmpty()) {
                this.store = new JSONArray(read());
            }
        } catch (Exception e) {
            SdpoLog.error(e);
        }
    }

    public JSONArray getStore() {
        return store;
    }

    public void saveInspection(JSONObject inspection) {
        store.put(inspection);
        this.save();
    }

    public void save() {
        try {
            create();
            this.writeFile(store.toString(1));
        } catch (IOException e) {
            SdpoLog.error(e);
        }
    }

    public JSONObject storeObjects() {
        return this.storeObjects;
    }

    @Override
    public JSONArray getInspections(String driverHashId) {
        //todo фильтрация по id водителя
        return store;
    }
}
