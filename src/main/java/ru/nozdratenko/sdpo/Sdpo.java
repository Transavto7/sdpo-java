package ru.nozdratenko.sdpo;

import ru.nozdratenko.sdpo.file.FileConfiguration;

public class Sdpo {
    public static FileConfiguration mainConfig;

    public static void init() {
        initMainConfig();
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