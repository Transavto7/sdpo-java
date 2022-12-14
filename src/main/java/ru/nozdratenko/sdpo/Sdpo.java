package ru.nozdratenko.sdpo;

import org.springframework.stereotype.Component;
import org.usb4java.Context;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import ru.nozdratenko.sdpo.file.FileConfiguration;
import ru.nozdratenko.sdpo.helper.BrowserHelper;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;

@Component
public class Sdpo {
    public static FileConfiguration mainConfig;

    public static void init() {
        initMainConfig();
        BrowserHelper.openUrl("http://localhost:8080");
    }

    private static void initMainConfig() {
        FileConfiguration fileConfiguration = new FileConfiguration("configs/main.json");
        fileConfiguration.set("password", "7344946")
                .set("url", "https://test.ta-7.ru/api")
                .set("token", "$2y$10$2We7xBpaq1AxOhct.eTO4eq2G/winHxyNbfn.WsD7WbZw6rlMcLrS")
                .saveFile();
        mainConfig = fileConfiguration;
    }
}