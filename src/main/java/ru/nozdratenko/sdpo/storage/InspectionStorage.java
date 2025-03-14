package ru.nozdratenko.sdpo.storage;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class InspectionStorage extends FileBase implements StoreInLocalMemory {
    public JSONArray store = new JSONArray();


    public InspectionStorage() {
        this("storage/inspections.json");
    }

    public InspectionStorage(String path) {
        super(path);

        try {
            String str = read();
            if (!str.isEmpty()) {
                this.store = new JSONArray(read());
                Sdpo.settings.dynamicConfig.set("count_inspections", this.store.length());
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
        this.saveToLocalStorage();
    }

    @Override
    public void saveToLocalStorage() {
        try {
            this.create();
            this.writeFile(store.toString(1));
        } catch (IOException e) {
            SdpoLog.error(e);
        }
    }

    public void setStore(JSONArray store) {
        this.store = store;
    }
}
