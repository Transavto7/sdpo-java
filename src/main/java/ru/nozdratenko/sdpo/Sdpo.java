package ru.nozdratenko.sdpo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Settings.FileConfiguration;
import ru.nozdratenko.sdpo.Settings.SettingsContainer;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.helper.BrowserHelpers.BrowserHelper;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;
import ru.nozdratenko.sdpo.storage.DriverStorage;
import ru.nozdratenko.sdpo.storage.InspectionStorage;
import ru.nozdratenko.sdpo.storage.MedicStorage;
import ru.nozdratenko.sdpo.storage.StampStorage;
import ru.nozdratenko.sdpo.task.*;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.port.PortService.PortService;

import java.io.IOException;

@Component
public class Sdpo {
    private final AlcometerHelper alcometerHelper;
    private final PortService portService;
    private final BrowserHelper browserHelper;
    private final ThermometerHelper thermometerHelper;
    private final CameraHelper cameraHelper;

    @Autowired
    public Sdpo(
            AlcometerHelper alcometerHelper,
            PortService portService,
            BrowserHelper browserHelper,
            ThermometerHelper thermometerHelper,
            CameraHelper cameraHelper
    ) {
        this.alcometerHelper = alcometerHelper;
        this.portService = portService;
        this.browserHelper = browserHelper;
        this.thermometerHelper = thermometerHelper;
        this.cameraHelper = cameraHelper;
    }

    public static SettingsContainer settings;

    public static DriverStorage driverStorage;
    public static MedicStorage medicStorage;
    public static StampStorage serviceDataStorage;
    public static InspectionStorage inspectionStorage;

    public static final SaveStoreInspectionTask saveStoreInspectionTask = new SaveStoreInspectionTask();
    public static final MediaMakeTask mediaMakeTask = new MediaMakeTask();

    private static boolean connection = true;

    public void init() {
        SdpoLog.info("Run project");
        settings = SettingsContainer.init();
        runTasks();
        cameraHelper.initDimension();
        if (!this.portService.isAdmin() && !isAdmin()) {
            SdpoLog.error("The program has been started without admin role !!!");
        }
        alcometerHelper.init();
        alcometerHelper.setDeviceInstanceId();
        alcometerHelper.setComPort();
        thermometerHelper.setComPort();
    }

    public void runTasks() {
        mediaMakeTask.start();
//        runScannerTask();
    }

    public void loadData() {
        saveStoreInspectionTask.start();

        driverStorage = new DriverStorage();
        driverStorage.save();

        medicStorage = new MedicStorage();
        medicStorage.saveToLocalStorage();

        serviceDataStorage = new StampStorage();
        serviceDataStorage.saveToLocalStorage();

        inspectionStorage = new InspectionStorage();
        inspectionStorage.save();

        new Thread(() -> {
            try {
                driverStorage.load();
                driverStorage.save();

                medicStorage.getAllFromApi();
                medicStorage.saveToLocalStorage();

                serviceDataStorage.getAllFromApi();
                serviceDataStorage.saveToLocalStorage();
            } catch (IOException e) {
                SdpoLog.error(e);
            }
        }).start();
    }

    public void openBrowser() {
        new Thread(() -> {
            try {
                Thread.sleep(40000);
            } catch (InterruptedException e) {
                SdpoLog.error(e);
            }
            this.browserHelper.openUrl("http://localhost:8080");
        }).start();
    }

    public boolean isAdmin() {
        try {
            Process process = new ProcessBuilder("net", "session").start();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isConnection() {
        return connection;
    }

    public void setConnection(boolean connection) {
        Sdpo.connection = connection;
    }
}