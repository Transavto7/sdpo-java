package ru.nozdratenko.sdpo.Settings;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Settings.Factories.SettingsFactory;
import ru.nozdratenko.sdpo.Settings.Repository.SettingsEhzpoRepository;

public class SettingsContainer {
    public final FileConfiguration mainConfig;
    public final FileConfiguration ehzpoConnectConfig;
    public final FileConfiguration systemConfig;

    public SettingsContainer(FileConfiguration mainConfig, FileConfiguration ehzpoConnectConfig, FileConfiguration systemConfig) {
        this.mainConfig = mainConfig;
        this.ehzpoConnectConfig = ehzpoConnectConfig;
        this.systemConfig = systemConfig;
    }

    public static SettingsContainer init() {
        SettingsEhzpoRepository repository = SpringContext.getBean(SettingsEhzpoRepository.class);
        JSONObject defaultSettings = repository.getSettings();

        return new SettingsContainer(
            SettingsFactory.makeMain(defaultSettings),
            SettingsFactory.makeConnectionConfig(),
            SettingsFactory.makeSystem(defaultSettings)
        );
    }
}
