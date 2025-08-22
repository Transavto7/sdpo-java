package ru.nozdratenko.sdpo.Inspections.Technical.Controllers.ViewModels;

import lombok.Data;

@Data
public class LastMedicalInspection {
    private String driverHashId;
    private String fio;
    private String lastInspectionDate;
    private String typeView;
}
