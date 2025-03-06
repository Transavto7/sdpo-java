package ru.nozdratenko.sdpo.storage;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class EmployeeInspectionStorage extends FileBase {
    public JSONArray store = new JSONArray();
    public EmployeeInspectionStorage() {
        this("storage/employee_inspections.json");
    }

    public EmployeeInspectionStorage(String path) {
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

    public void putInspection(JSONObject inspection) {
        store.put(inspection);
    }

    public int count() {
        return this.store.length();
    }

    public void save() {
        try {
            this.create();
            this.writeFile(store.toString(1));
        } catch (IOException e) {
            SdpoLog.error(e);
        }
    }

    public JSONObject getInspection(int index) {
        return this.store.getJSONObject(index);
    }

    public void remove(int index) {
        this.store.remove(index);
    }
}
