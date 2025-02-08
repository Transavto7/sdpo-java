package ru.nozdratenko.sdpo.storage.repository.stamp;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class StampLocalStorageRepository extends FileBase {

    protected static String filePath = "storage/stamps.json";
    protected Long lastUpdate = 0L;

    protected JSONObject store = new JSONObject();

    public StampLocalStorageRepository() {
        super(StampLocalStorageRepository.filePath);

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
