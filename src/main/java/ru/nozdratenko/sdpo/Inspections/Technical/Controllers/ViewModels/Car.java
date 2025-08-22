package ru.nozdratenko.sdpo.Inspections.Technical.Controllers.ViewModels;

import lombok.Data;

@Data
public class Car {
    private String hashId;
    private String gosNumber;
    private String markModel;
    private String typeAuto;
    private String vin;
    private String lastInspectionDate;
    private int lastOdometerValue;
}
