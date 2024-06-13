package ru.nozdratenko.sdpo.storage.repository.inspection;

import ru.nozdratenko.sdpo.Sdpo;

public class InspectionRepositoryFactory {

    public static InspectionRepositoryInterface get() {
        if (Sdpo.isConnection()) {
            return new InspectionRemoteRepository();
        } else {
            return new InspectionLocalStorageRepository();
        }
    }
}
