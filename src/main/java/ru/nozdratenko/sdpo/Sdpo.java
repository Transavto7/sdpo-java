package ru.nozdratenko.sdpo;

import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.file.FileConfiguration;
import ru.nozdratenko.sdpo.helper.BrowserHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Component
public class Sdpo {
    public static FileConfiguration mainConfig;
    public static FileConfiguration systemConfig;

    public static void init() {
        initMainConfig();
        initSystemConfig();
        BrowserHelper.openUrl("http://localhost:8080");
        SdpoLog.info("Run project");
    }

    private static void initMainConfig() {
        FileConfiguration fileConfiguration = new FileConfiguration("configs/main.json");
        fileConfiguration.setDefault("password", "7344946")
                .setDefault("url", "https://test.ta-7.ru/api")
                .setDefault("token", "$2y$10$2We7xBpaq1AxOhct.eTO4eq2G/winHxyNbfn.WsD7WbZw6rlMcLrS")
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
                .setDefault("printer_write", true)
                .setDefault("thermometer_skip", true)
                .setDefault("thermometer_visible", true)
                .saveFile();
        systemConfig = fileConfiguration;
    }
}