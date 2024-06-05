package ru.nozdratenko.sdpo.storage;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.storage.repository.stamp.StampLocalStorageRepository;
import ru.nozdratenko.sdpo.storage.repository.stamp.StampRemoteRepository;

import java.io.IOException;

public class StampStorage implements RemoteServerRequest, StoreInLocalMemory {

    protected JSONObject data;

    protected StampLocalStorageRepository localStorageRepository = new StampLocalStorageRepository();

    public StampStorage() {
        this.data = localStorageRepository.getStore();
    }

    public void saveToLocalStorage() {
        this.localStorageRepository.store(this.data);
    }

    public JSONObject getDataFromLocalStorage() {
        return localStorageRepository.getStore();
    }

    public void loadFromApi() throws IOException {
        this.data = (new StampRemoteRepository()).all();
    }

}
