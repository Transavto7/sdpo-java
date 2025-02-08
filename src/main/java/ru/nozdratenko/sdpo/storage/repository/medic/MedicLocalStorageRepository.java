package ru.nozdratenko.sdpo.storage.repository.medic;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class MedicLocalStorageRepository extends FileBase {

    protected static String filePath = "storage/medics.json";
    protected Long lastUpdate = 0L;

    protected JSONObject store = new JSONObject();

    public MedicLocalStorageRepository() {
        super(MedicLocalStorageRepository.filePath);

        try {
            String str = read();
            if (!str.isEmpty()) {
                JSONObject result = new JSONObject(read());
                if (result.has("data")) {
                    this.store = result.getJSONObject("data");
                }
            }
        } catch (Exception e) {
            SdpoLog.error(e);
        }
    }

    public JSONObject getStore() {
        return store;
    }

    public void store(JSONObject data) {
        try {
            create();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("last_update", this.lastUpdate);
            jsonObject.put("data", data);
            this.writeFile(jsonObject.toString(1));
        } catch (IOException e) {
            SdpoLog.error(e);
        }
    }
}
