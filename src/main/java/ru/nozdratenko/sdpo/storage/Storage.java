package ru.nozdratenko.sdpo.storage;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class Storage extends FileBase {
    protected HashMap<String, JSONObject> store = new HashMap<>();
    protected Long lastUpdate = 0L;

    public Storage(String path) {
        super(path);

        try {
            String str = read();
            if (!str.isEmpty()) {
                JSONObject result = new JSONObject(read());
                if (result.has("data")) {
                    JSONArray array = result.getJSONArray("data");
                    formatDataLoad(array);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SdpoLog.error(e);
        }
    }

    public void loadApi(String url) throws IOException {
        Request request = new Request(url);
        try {
            String response = request.sendGet();
            JSONArray array = new JSONArray(response);
            formatDataLoad(array);
            this.lastUpdate = new Date().getTime();
        } catch (Exception | ApiException e) {
            SdpoLog.error("Error load storage data");
        }
    }

    public void save() {
        try {
            create();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("last_update", this.lastUpdate);
            jsonObject.put("data", formatDataSave());
            this.writeFile(jsonObject.toString(1));
        } catch (IOException e) {
            SdpoLog.error(e);
        }
    }

    protected JSONArray formatDataSave() {
        return new JSONArray();
    }

    protected void formatDataLoad(JSONArray jsonArray) {

    }

    public void setStore(HashMap<String, JSONObject> store) {
        this.store = store;
    }

    public HashMap<String, JSONObject> getStore() {
        return store;
    }
}
