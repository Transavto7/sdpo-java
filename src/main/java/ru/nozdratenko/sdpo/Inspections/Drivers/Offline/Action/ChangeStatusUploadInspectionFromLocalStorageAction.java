package ru.nozdratenko.sdpo.Inspections.Drivers.Offline.Action;

import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Inspections.Exceptions.InspectionNotFound;
import ru.nozdratenko.sdpo.Inspections.Drivers.Offline.ResendStatusEnum;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionLocalStorageRepository;

@AllArgsConstructor
public class ChangeStatusUploadInspectionFromLocalStorageAction {
    private final ResendStatusEnum statusEnum;
    private final int index;

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
