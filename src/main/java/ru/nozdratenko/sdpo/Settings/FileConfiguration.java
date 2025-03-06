package ru.nozdratenko.sdpo.Settings;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.Settings.Contracts.Configuration;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;

public class FileConfiguration extends FileBase implements Configuration {
    protected JSONObject json = new JSONObject();

    public FileConfiguration(String path) {
        super(path);
        try {
            String str = read();
            if (!str.isEmpty()) {
                try {
                    json = new JSONObject(str);
                } catch (Exception e) {
                    SdpoLog.info("Error for parse configuration json:  " + str);
                    throw e;
                }
            }
        } catch (Exception e) {
            json = new JSONObject();
            SdpoLog.error(e);
        }
    }

    public FileConfiguration set(String key, String value) {
        json.put(key, value);
        return this;
    }

    public FileConfiguration set(String key, Object value) {
        json.put(key, value);
        return this;
    }

    public FileConfiguration set(String key, int value) {
        json.put(key, value);
        return this;
    }
    public FileConfiguration set(String key, boolean value) {
        json.put(key, value);
        return this;
    }

    public FileConfiguration setDefault(String key, String value) {
        if (!json.has(key)) {
            json.put(key, value);
        }
        return this;
    }

    public FileConfiguration setDefault(String key, int value) {
        if (!json.has(key)) {
            json.put(key, value);
        }
        return this;
    }

    public FileConfiguration setDefault(String key, boolean value) {
        if (!json.has(key)) {
            json.put(key, value);
        }
        return this;
    }

    public String getString(String key) {
        if (json.has(key)) {
            return json.get(key).toString();
        }

        return "";
    }

    public int getInt(String key) {
        if (json.has(key)) {
            return json.getInt(key);
        }

        return 0;
    }

    public boolean getBoolean(String key) {
        if (json.has(key)) {
            return json.getBoolean(key);
        }

        return false;
    }

    public JSONObject getJson() {
        return json;
    }

    public FileConfiguration saveFile() {
        try {
            create();
            String settings = json.toString(1);
            this.writeFile(settings);
            SdpoLog.info("Write settings to file successfully!");
        } catch (IOException e) {
            SdpoLog.error(e);
        }

        return this;
    }

    public FileConfiguration mergeWithJson(JSONObject mainJson) {
        if (mainJson != null) {
            for (String key : mainJson.keySet()) {
                json.put(key, mainJson.get(key));
            }
        }

        return this;
    }
}
