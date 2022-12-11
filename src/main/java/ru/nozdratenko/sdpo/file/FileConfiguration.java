package ru.nozdratenko.sdpo.file;

import org.json.JSONObject;

import java.io.IOException;

public class FileConfiguration extends FileBase {
    protected JSONObject json = new JSONObject();

    public FileConfiguration(String path) {
        super(path);
    }

    public FileConfiguration set(String key, String value) {
        json.put(key, value);
        return this;
    }

    public FileConfiguration set(String key, int value) {
        json.put(key, value);
        return this;
    }

    public String getString(String key) {
        if (json.has(key)) {
            return json.getString(key);
        }

        return "";
    }

    public int getInt(String key) {
        if (json.has(key)) {
            return json.getInt(key);
        }

        return 0;
    }

    public JSONObject getJson() {
        return json;
    }

    public FileConfiguration saveFile() {
        try {
            create();
            this.writeFile(json.toString(10));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }
}
