package ru.nozdratenko.sdpo;

import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.file.FileConfiguration;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.helper.BrowserHelper;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;
import ru.nozdratenko.sdpo.storage.DriverStorage;
import ru.nozdratenko.sdpo.storage.InspectionStorage;
import ru.nozdratenko.sdpo.storage.MedicStorage;
import ru.nozdratenko.sdpo.task.*;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Scanner;

@Component
public class Sdpo {
    public static FileConfiguration mainConfig;
    public static FileConfiguration systemConfig;
    public static DriverStorage driverStorage;
    public static MedicStorage medicStorage;
    public static InspectionStorage inspectionStorage;

    public static final TonometerResultTask tonometerResultTask = new TonometerResultTask();
    public static final TonometerConnectTask tonometerConnectTask = new TonometerConnectTask();
    public static final ThermometerResultTask thermometerResultTask = new ThermometerResultTask();
    public static final AlcometerResultTask alcometerResultTask = new AlcometerResultTask();
    public static final SaveStoreInspectionTask saveStoreInspectionTask = new SaveStoreInspectionTask();
    public static final MediaMakeTask mediaMakeTask = new MediaMakeTask();

    private static boolean connection = true;

    public static void init() {
        SdpoLog.info("Run project");
        initMainConfig();
        initSystemConfig();
        runTasks();
        CameraHelper.initDimension();
        AlcometerHelper.setComPort();
        ThermometerHelper.setComPort();
    }


    public static void runTasks() {
        tonometerResultTask.start();
        thermometerResultTask.start();
        alcometerResultTask.start();
        tonometerConnectTask.start();
        mediaMakeTask.start();
        runScannerTask();
    }

    public static void loadData() {
        saveStoreInspectionTask.start();

        driverStorage = new DriverStorage();
        driverStorage.save();

        medicStorage = new MedicStorage();
        medicStorage.save();

        inspectionStorage = new InspectionStorage();
        inspectionStorage.save();

        new Thread(() -> {
            try {
                driverStorage.loadApi();
                driverStorage.save();

                medicStorage.loadApi();
                medicStorage.save();
            } catch (IOException e) {
                SdpoLog.error(e);
            }
        }).start();
    }

    public static void openBrowser() {
        new Thread(() -> {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) { }
            BrowserHelper.openUrl("http://localhost:8080");
        }).start();
    }

    /**
     * Временная заглшука, чтобы убрать зависание консоли
     */
    private static void runScannerTask() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (!input.isEmpty()) {
                        // обработка ввода
                    }
                }
            }
        }).start();
    }

    private static void initMainConfig() {
        FileConfiguration fileConfiguration = new FileConfiguration("configs/main.json");
        fileConfiguration.setDefault("password", "7344946")
                .setDefault("medic_password", "0000000")
                .setDefault("run_browser_cmd",
                        "\"C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe\" --kiosk " +
                                "${url} --edge-kiosk-type=fullscreen -inprivate")
                .setDefault("url", "https://test.ta-7.ru/api")
                .setDefault("token", "$2y$10$2We7xBpaq1AxOhct.eTO4eq2G/winHxyNbfn.WsD7WbZw6rlMcLrS")
                .setDefault("tonometer_mac", null)
                .setDefault("tonometer_connect", false)
                .saveFile();
        mainConfig = fileConfiguration;
    }

    private static void initSystemConfig() {
        FileConfiguration fileConfiguration = new FileConfiguration("configs/system.json");
        fileConfiguration.setDefault("driver_info", false)
                .setDefault("type_ride", true)
                .setDefault("question_sleep", true)
                .setDefault("question_helth", true)
                .setDefault("alcometer_fast", false)
                .setDefault("alcometer_skip", true)
                .setDefault("alcometer_visible", true)
                .setDefault("tonometer_skip", true)
                .setDefault("tonometer_visible", true)
                .setDefault("camera_video", true)
                .setDefault("camera_photo", true)
                .setDefault("driver_photo", false)
                .setDefault("camera_dimension", CameraHelper.getDefaultSize())
                .setDefault("printer_write", true)
                .setDefault("print_count", 1)
                .setDefault("thermometer_skip", true)
                .setDefault("thermometer_visible", true)
                .setDefault("manual_mode", false)
                .setDefault("auto_start", true)
                .saveFile();
        systemConfig = fileConfiguration;
    }

    public static boolean isConnection() {
        return connection;
    }

    public static void setConnection(boolean connection) {
        Sdpo.connection = connection;
    }
}