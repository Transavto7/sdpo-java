package ru.nozdratenko.sdpo.Settings.Factories;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.Settings.CoreConfigurations.FileConfiguration;
import ru.nozdratenko.sdpo.Settings.CoreConfigurations.MemoryConfiguration;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SettingsFactory {
    public static FileConfiguration makeMain(JSONObject defaultSettings) {
        FileConfiguration configuration = new FileConfiguration("configs/main.json");
        configuration.setDefault("password", SettingsFactory.getValue(defaultSettings, "password", "7344946"))
            .setDefault("medic_password", SettingsFactory.getValue(defaultSettings, "medic_password", "0000000"))
            .setDefault("support_phone", SettingsFactory.getValue(defaultSettings, "support_phone", "8 (800) 777-72-46"))
            .setDefault("terminal_is_blocked", SettingsFactory.getValue(defaultSettings, "terminal_is_blocked", false))
            .setDefault("run_browser_cmd",
                SettingsFactory.getValue(defaultSettings,
                    "run_browser_cmd",
                    "\"C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe\" --kiosk " +
                        "${url} --edge-kiosk-type=fullscreen -inprivate"
                ))
            .setDefault("tonometer_mac", null)
            .setDefault("tonometer_connect", false)
            .mergeWithJson(defaultSettings)
            .saveFile();

        if (!configuration.getString("url").isEmpty()) {
            Sdpo.connectionConfig.set("url", configuration.getString("url"));
            Sdpo.connectionConfig.set("token", configuration.getString("token"));
            configuration.getJson().remove("url");
            configuration.getJson().remove("token");
            configuration.saveFile();
            Sdpo.connectionConfig.saveFile();
        }

        return configuration;
    }

    public static FileConfiguration makeConnectionConfig() {
        FileConfiguration configuration = new FileConfiguration("configs/connect.json");
        configuration.setDefault("url", "https://test.ta-7.ru/api")
//                .setDefault("token", "$2y$10$4tO4QmPV7eU2VHKdDbUWVusMa7Dwui3IGYi69UPoGrYsWrM3KMda.")
            .setDefault("token", "$2y$10$2We7xBpaq1AxOhct.eTO4eq2G/winHxyNbfn.WsD7WbZw6rlMcLrS")
            .saveFile();

        return configuration;
    }

    public static FileConfiguration makeSystem(JSONObject defaultSettings) {
        CameraHelper cameraHelper = SpringContext.getBean(CameraHelper.class);
        FileConfiguration configuration = new FileConfiguration("configs/system.json");

        configuration.setDefault("driver_info", SettingsFactory.getValue(defaultSettings, "driver_info", false))
            .setDefault("type_ride", SettingsFactory.getValue(defaultSettings, "type_ride", true))
            .setDefault("question_sleep", SettingsFactory.getValue(defaultSettings, "question_sleep", false))
            .setDefault("question_helth", SettingsFactory.getValue(defaultSettings, "question_helth", false))
            .setDefault("alcometer_fast", SettingsFactory.getValue(defaultSettings, "alcometer_fast", true))
            .setDefault("alcometer_skip", SettingsFactory.getValue(defaultSettings, "alcometer_skip", false))
            .setDefault("alcometer_retry", SettingsFactory.getValue(defaultSettings, "alcometer_retry", true))
            .setDefault("alcometer_visible", SettingsFactory.getValue(defaultSettings, "alcometer_visible", true))
            .setDefault("tonometer_skip", SettingsFactory.getValue(defaultSettings, "tonometer_skip", false))
            .setDefault("tonometer_visible", SettingsFactory.getValue(defaultSettings, "tonometer_visible", true))
            .setDefault("tonometer_logs_visible", SettingsFactory.getValue(defaultSettings, "tonometer_logs_visible", true))
            .setDefault("camera_video", SettingsFactory.getValue(defaultSettings, "camera_video", true))
            .setDefault("check_phone_number", SettingsFactory.getValue(defaultSettings, "check_phone_number", true))
            .setDefault("camera_photo", SettingsFactory.getValue(defaultSettings, "camera_photo", true))
            .setDefault("driver_photo", SettingsFactory.getValue(defaultSettings, "driver_photo", false))
            .setDefault("camera_dimension", cameraHelper.getDefaultSize())
            .setDefault("printer_write", SettingsFactory.getValue(defaultSettings, "printer_write", true))
            .setDefault("print_qr_check", SettingsFactory.getValue(defaultSettings, "print_qr_check", false))
            .setDefault("print_count", SettingsFactory.getValue(defaultSettings, "print_count", 1))
            .setDefault("thermometer_skip", SettingsFactory.getValue(defaultSettings, "driver_info", false))
            .setDefault("thermometer_visible", SettingsFactory.getValue(defaultSettings, "thermometer_visible", true))
            .setDefault("manual_mode", SettingsFactory.getValue(defaultSettings, "manual_mode", false))
            .setDefault("auto_start", SettingsFactory.getValue(defaultSettings, "auto_start", true))
            .setDefault("delay_day_in_offline_mod", SettingsFactory.getValue(defaultSettings, "delay_day_in_offline_mod", 30))
            .setDefault("max_inspection_in_offline_mod", SettingsFactory.getValue(defaultSettings, "max_inspection_in_offline_mod", 300))
            .setDefault("auto_send_to_crm", SettingsFactory.getValue(defaultSettings, "auto_send_to_crm", true))
            .setDefault("date_verification", null)
            .setDefault("cursor", false)
            .setDefault("delay_before_retry_inspection", SettingsFactory.getValue(defaultSettings, "delay_before_retry_inspection", 5000))
            .setDefault("delay_before_redirect_to_main_page", SettingsFactory.getValue(defaultSettings, "delay_before_redirect_to_main_page", 10000))
            .mergeWithJson(defaultSettings)
            .saveFile();

        return configuration;
    }

    public static FileConfiguration makeDynamic() {
        FileConfiguration configuration = new FileConfiguration("configs/dynamic.json");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());

        configuration.setDefault("last_online", currentDate)
            .setDefault("count_inspections", 0)
            .saveFile();

        return configuration;
    }

    private static String getValue(JSONObject defaultSettings, String key, String defaultValue) {
        if (defaultSettings != null && defaultSettings.has(key)) {
            return defaultSettings.getString(key);
        }

        return defaultValue;
    }

    private static int getValue(JSONObject defaultSettings, String key, int defaultValue) {
        if (defaultSettings != null && defaultSettings.has(key)) {
            return defaultSettings.getInt(key);
        }

        return defaultValue;
    }

    private static boolean getValue(JSONObject defaultSettings, String key, boolean defaultValue) {
        if (defaultSettings != null && defaultSettings.has(key)) {
            return defaultSettings.getBoolean(key);
        }

        return defaultValue;
    }

    public static MemoryConfiguration makeTemporary() {
        MemoryConfiguration configuration = new MemoryConfiguration();

        return configuration;
    }
}
