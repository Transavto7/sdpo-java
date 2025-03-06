package ru.nozdratenko.sdpo.Inspections.Employees.InspectionSavers;

import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Inspections.Employees.Serializers.EmployeeInspectionRequestSerializer;
import ru.nozdratenko.sdpo.Inspections.Employees.Serializers.EmployeeInspectionResponseSerializer;
import ru.nozdratenko.sdpo.Sdpo;

public class EmployeeInspectionSaversBuilder {
    public static EmployeeInspectionSaver build() {
        if (Sdpo.isConnection()) {
            return new EmployeeOnlineInspectionSaver(
                    SpringContext.getBean(EmployeeInspectionRequestSerializer.class),
                    SpringContext.getBean(EmployeeInspectionResponseSerializer.class)
            );
        } else {
            return new EmployeeOfflineInspectionSaver();
        }
    }
}
