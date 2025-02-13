package ru.nozdratenko.sdpo.InspectionManager.Offline.Action;

import ru.nozdratenko.sdpo.InspectionManager.Exception.InspectionNotFound;
import ru.nozdratenko.sdpo.storage.repository.inspection.InspectionLocalStorageRepository;

public class DeleteInspectionFromLocalStorageAction {

    private final int index;
    private final InspectionLocalStorageRepository repository = new InspectionLocalStorageRepository();

    public DeleteInspectionFromLocalStorageAction(int index) {
        this.index = index;
    }

    public void handle() {
        repository.deleteInspectionByIndex(this.index);
    }


    public static void deleteInspectionByIndex(int index) throws InspectionNotFound {
        new DeleteInspectionFromLocalStorageAction(index).handle();
    }

    public static void deleteInspectionByDriverIdAndCreateDate(String driverId, String createdDate) throws InspectionNotFound {
        int index = (new InspectionLocalStorageRepository()).getInspectionsIndexByDriverIdAndCreatedDate(driverId, createdDate);
        new DeleteInspectionFromLocalStorageAction(index).handle();
    }
}
