package ru.nozdratenko.sdpo.Settings;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Settings.CoreConfigurations.FileConfiguration;
import ru.nozdratenko.sdpo.Settings.CoreConfigurations.MemoryConfiguration;
import ru.nozdratenko.sdpo.Settings.Factories.SettingsFactory;
import ru.nozdratenko.sdpo.Settings.Repository.SettingsEhzpoRepository;
import ru.nozdratenko.sdpo.storage.repository.stamp.StampRemoteRepository;

import java.util.Optional;

public class SettingsContainer {
    public final FileConfiguration mainConfig;
    public final FileConfiguration systemConfig;
    public final FileConfiguration dynamicConfig;
    public final MemoryConfiguration temporaryConfig;

    public SettingsContainer(FileConfiguration mainConfig, FileConfiguration systemConfig, FileConfiguration dynamicConfig, MemoryConfiguration temporaryConfig) {
        this.mainConfig = mainConfig;
        this.systemConfig = systemConfig;
        this.dynamicConfig = dynamicConfig;
        this.temporaryConfig = temporaryConfig;
    }

    public static SettingsContainer init() {
        SettingsEhzpoRepository repository = SpringContext.getBean(SettingsEhzpoRepository.class);
        StampRemoteRepository stampRemoteRepository = SpringContext.getBean(StampRemoteRepository.class);
        JSONObject defaultSettings = Optional.ofNullable(repository.getSettings()).orElse(new JSONObject());

        FileConfiguration main = SettingsFactory.makeMain(defaultSettings.optJSONObject("main"));

        if (!main.getJson().has("selected_stamp") || main.getJson().get("selected_stamp") == null) {
            JSONObject stamp = stampRemoteRepository.get();
            main.getJson().put("selected_stamp", stamp);
            main.saveFile();
        }

        return new SettingsContainer(
                main,
                SettingsFactory.makeSystem(defaultSettings.optJSONObject("system")),
                SettingsFactory.makeDynamic(),
                SettingsFactory.makeTemporary()
        );
    }
}
