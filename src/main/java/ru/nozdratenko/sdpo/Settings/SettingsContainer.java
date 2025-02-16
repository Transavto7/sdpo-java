package ru.nozdratenko.sdpo.Settings;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Settings.Factories.SettingsFactory;
import ru.nozdratenko.sdpo.Settings.Repository.SettingsEhzpoRepository;
import ru.nozdratenko.sdpo.storage.repository.stamp.StampRemoteRepository;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.Optional;

public class SettingsContainer {
    public final FileConfiguration mainConfig;
    public final FileConfiguration systemConfig;

    public SettingsContainer(FileConfiguration mainConfig, FileConfiguration systemConfig) {
        this.mainConfig = mainConfig;
        this.systemConfig = systemConfig;
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
                SettingsFactory.makeSystem(defaultSettings.optJSONObject("system"))
        );
    }
}
