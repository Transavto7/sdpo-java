package ru.nozdratenko.sdpo.Cars.ViewModels;

import lombok.Data;

import java.util.Date;

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
