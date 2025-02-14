package ru.nozdratenko.sdpo.InspectionManager.Offline.Action;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.InspectionManager.Exception.InspectionNotFound;
import ru.nozdratenko.sdpo.InspectionManager.Offline.ResendStatusEnum;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionLocalStorageRepository;

public class ChangeStatusUploadInspectionFromLocalStorageAction {
    private int index;
    private ResendStatusEnum statusEnum;

    public ChangeStatusUploadInspectionFromLocalStorageAction(ResendStatusEnum statusEnum, int index) {
        this.index = index;
        this.statusEnum = statusEnum;
    }

    public void handle() {
        Sdpo.inspectionStorage.setStore(this.changeStatus(Sdpo.inspectionStorage.getStore()));
        Sdpo.inspectionStorage.saveToLocalStorage();
    }

    private JSONArray changeStatus(JSONArray store) {
        JSONObject item = (JSONObject) store.get(this.index);
        item.put("status_send", this.statusEnum);
        store.put(this.index, item);
        return store;
    }


    public static void changeStatusByDriverIdAndCreateDate(String driverId, String createdDate, ResendStatusEnum statusEnum) throws InspectionNotFound {
        int index = (new InspectionLocalStorageRepository()).getInspectionsIndexByDriverIdAndCreatedDate(driverId, createdDate);
        new ChangeStatusUploadInspectionFromLocalStorageAction(statusEnum, index).handle();
    }
}
