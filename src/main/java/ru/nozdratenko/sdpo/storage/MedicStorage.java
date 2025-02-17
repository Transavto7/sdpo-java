package ru.nozdratenko.sdpo.storage;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.storage.repository.medic.MedicLocalStorageRepository;
import ru.nozdratenko.sdpo.storage.repository.medic.MedicRemoteRepository;

import java.io.IOException;

public class MedicStorage implements RemoteServerRequest, StoreInLocalMemory {

    protected JSONObject data;

    protected MedicLocalStorageRepository localStorageRepository = new MedicLocalStorageRepository();

    public MedicStorage() {
        this.data = localStorageRepository.getStore();
    }

    public void saveToLocalStorage() {
        this.localStorageRepository.store(this.data);
    }

    public JSONObject getDataFromLocalStorage() {
        return localStorageRepository.getStore();
    }
    public void getAllFromApi() throws IOException {
        JSONObject data = (new MedicRemoteRepository()).get();
        if (data != null) {
            this.data = data;
        }
    }

}
