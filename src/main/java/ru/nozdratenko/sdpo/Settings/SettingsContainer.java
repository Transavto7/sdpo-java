package ru.nozdratenko.sdpo.Settings;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Settings.Factories.SettingsFactory;
import ru.nozdratenko.sdpo.Settings.Repository.SettingsEhzpoRepository;

import java.util.Optional;

public class SettingsContainer {
    public final FileConfiguration mainConfig;
    public final FileConfiguration systemConfig;

    public SettingsContainer(FileConfiguration mainConfig, FileConfiguration systemConfig) {
        this.mainConfig = mainConfig;
        this.systemConfig = systemConfig;
    }

    public static SettingsContainer init() {
//        SettingsEhzpoRepository repository = SpringContext.getBean(SettingsEhzpoRepository.class);
//        JSONObject defaultSettings = Optional.ofNullable(repository.getSettings()).orElse(new JSONObject());

        return new SettingsContainer(
//            SettingsFactory.makeMain(defaultSettings.optJSONObject("main")),
//            SettingsFactory.makeSystem(defaultSettings.optJSONObject("system"))
                SettingsFactory.makeMain(new JSONObject()),
                SettingsFactory.makeSystem(new JSONObject())
        );
    }
}
