package ru.nozdratenko.sdpo.Inspections.Employees.Storages;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.storage.Storage;

import java.io.IOException;

public class EmployeeStorage extends Storage {
    public EmployeeStorage(String path) {
        super(path);
    }

    public EmployeeStorage() {
        super("storage/employees.json");
    }

    @Override
    public void formatDataLoad(JSONArray array) {
        for (Object obj : array) {
            if (obj instanceof JSONObject) {
                JSONObject json = (JSONObject) obj;
                if (json.has("hash_id") && json.has("fio")) {
                    store.put(json.get("hash_id").toString(), json);
                }
            }
        }
    }

    @Override
    protected JSONArray formatDataSave() {
        JSONArray result = new JSONArray();

        for (String key : this.store.keySet()) {
            result.put(this.store.get(key));
        }

        return result;
    }

    public void load() throws IOException {
        this.loadApi("sdpo/employees");
    }
}
