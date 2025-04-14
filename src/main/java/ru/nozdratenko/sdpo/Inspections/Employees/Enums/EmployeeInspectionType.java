package ru.nozdratenko.sdpo.Inspections.Employees.Enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nozdratenko.sdpo.Inspections.Employees.Storages.EmployeeInspectionStorage;

@RequiredArgsConstructor
@Getter
public enum EmployeeInspectionType {
    OPEN ("open"),
    CLOSE ("close");

    private final String title;

    @Override
    public String toString() {
        return title;
    }

    public static EmployeeInspectionType fromTitle(String title) {
        for (EmployeeInspectionType type : EmployeeInspectionType.values()) {
            if (type.getTitle().equalsIgnoreCase(title)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown title: " + title);
    }
}
