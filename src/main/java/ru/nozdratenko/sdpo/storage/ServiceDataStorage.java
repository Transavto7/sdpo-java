package ru.nozdratenko.sdpo.storage;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Date;

public class ServiceDataStorage extends FileBase implements RemoteServerRequest, StoreInLocalMemory {

    protected JSONObject store = new JSONObject();
    protected Long lastUpdate = 0L;

    public ServiceDataStorage() {
        this("storage/service.json");
    }

    public ServiceDataStorage(String path) {
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

    public void save() {
        this.saveToLocalStorage();
    }

    public JSONObject getStore() {
        return store;
    }

    public void setStore(JSONObject store) {
        this.store = store;
    }

    public void load() throws IOException {
        this.loadApi("sdpo/stamp");
    }


    @Override
    public void loadApi(String url) throws IOException {
        try {
            Request request = new Request(url);
            String response = request.sendGet();
            this.store = new JSONObject(response);
            this.lastUpdate = new Date().getTime();
        } catch (ApiException | Exception e) {
            SdpoLog.error("Error load service list");
        }
    }

    @Override
    public void saveToLocalStorage() {
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
}
