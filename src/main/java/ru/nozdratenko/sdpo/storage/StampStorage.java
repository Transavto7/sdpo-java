package ru.nozdratenko.sdpo.storage;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.storage.repository.stamp.StampLocalStorageRepository;
import ru.nozdratenko.sdpo.storage.repository.stamp.StampRemoteRepository;

import java.io.IOException;
import java.util.Map;

public class StampStorage implements RemoteServerRequest, StoreInLocalMemory {

    protected JSONObject data;

    protected StampLocalStorageRepository localStorageRepository = new StampLocalStorageRepository();
    protected StampRemoteRepository remoteRepository = new StampRemoteRepository();

    public StampStorage() {
        this.data = localStorageRepository.getStore();
    }

    public void saveToLocalStorage() {
        this.localStorageRepository.store(this.data);
    }

    public JSONObject getDataFromLocalStorage() {
        return localStorageRepository.getStore();
    }

    public void getAllFromApi() throws IOException {
        this.data = remoteRepository.all();
    }

    public JSONObject getFromApi() {
        return remoteRepository.get();
    }

    public void selectStamp(JSONObject stamp) {
        Sdpo.settings.mainConfig.getJson().put("selected_stamp", stamp);
        Sdpo.settings.mainConfig.saveFile();
    }

}
