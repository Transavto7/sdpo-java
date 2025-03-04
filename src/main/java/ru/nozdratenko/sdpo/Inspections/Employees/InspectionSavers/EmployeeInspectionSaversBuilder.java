package ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers;

import ru.nozdratenko.sdpo.Sdpo;

public class EmployeeInspectionSaversBuilder {
    public static EmployeeInspectionSaver build() {
        if (Sdpo.isConnection()) {
            return new EmployeeOnlineInspectionSaver();
        } else {
            return new EmployeeOfflineInspectionSaver();
        }
    }
}
