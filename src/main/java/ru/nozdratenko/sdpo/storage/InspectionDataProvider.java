package ru.nozdratenko.sdpo.storage;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class InspectionDataProvider extends FileBase {
    public JSONArray store = new JSONArray();
    public JSONObject storeObjects = new JSONObject();


    public InspectionDataProvider() {
        this("storage/inspections.json");

    }

    public InspectionDataProvider(String path) {
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

    public void getInspectionsFromStorageWithObjectFormat(String path) {
        this.path = path;
        try {
            String str = read();
            if (!str.isEmpty()) {
                JSONObject result = new JSONObject(read());
                if (result.has("data")) {
                    this.storeObjects = result.getJSONObject("data");
                }
            }
        } catch (Exception e) {
            SdpoLog.error(e);
        }
    }
}
