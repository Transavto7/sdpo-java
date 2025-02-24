package ru.nozdratenko.sdpo.InspectionManager.InspectionSavers;

import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;

public class InspectionSaversBuilder {
    public static InspectionSaver build() {
        PrinterHelper printerHelper = SpringContext.getBean(PrinterHelper.class);

        if (!Sdpo.isConnection()) {
            return new OfflineInspectionSaver(printerHelper);
        } else {
            if (Sdpo.settings.systemConfig.getBoolean("manual_mode")) {
                return new PackInspectionSaver(printerHelper);
            } else {
                return new OnlineInspectionSaver(printerHelper);
            }
        }
    }
}
