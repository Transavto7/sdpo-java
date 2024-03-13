package ru.nozdratenko.sdpo.storage;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class MedicStorage extends FileBase {
    protected JSONObject store = new JSONObject();
    protected Long lastUpdate = 0L;

    public MedicStorage() {
        this("storage/medics.json");
    }

    public MedicStorage(String path) {
        super(path);

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

    public void loadApi(String url) throws IOException {
        try {
            Request request = new Request(url);
            String response = request.sendGet();
            this.store = new JSONObject(response);
            this.lastUpdate = new Date().getTime();
        } catch (ApiException | Exception e) {
            SdpoLog.error("Error load medic list");
        }
    }

    public void save() {
        try {
            create();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("last_update", this.lastUpdate);
            jsonObject.put("data", this.store);
            this.writeFile(jsonObject.toString(1));
        } catch (IOException e) {
            SdpoLog.error(e);
        }
    }

    public void setStore(JSONObject store) {
        this.store = store;
    }

    public JSONObject getStore() {
        return store;
    }

    public void loadApi() throws IOException {
        this.loadApi("sdpo/medics");
    }
}
