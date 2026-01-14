package ru.nozdratenko.sdpo.Inspections.Drivers.InspectionSavers;

import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers.EnvironmentMonitorHelper;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;

public class DriverInspectionSaversBuilder {
    public static DriverInspectionSaver build() {
        PrinterHelper printerHelper = SpringContext.getBean(PrinterHelper.class);
        EnvironmentMonitorHelper environmentMonitorHelper = SpringContext.getBean(EnvironmentMonitorHelper.class);

        return new PackInspectionSaver(printerHelper, environmentMonitorHelper);
    }
}
