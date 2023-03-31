package ru.nozdratenko.sdpo.storage;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class InspectionStorage extends FileBase {
    public JSONArray store = new JSONArray();

    public InspectionStorage() {
        this("storage/inspections.json");

    }

    public InspectionStorage(String path) {
        super(path);

        try {
            String str = read();
            if (!str.isEmpty()) {
                JSONArray result = new JSONArray(read());
                this.store = result;
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
}
