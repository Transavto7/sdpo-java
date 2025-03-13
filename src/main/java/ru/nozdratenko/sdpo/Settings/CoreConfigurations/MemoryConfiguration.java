package ru.nozdratenko.sdpo.Settings.CoreConfigurations;

import org.json.JSONObject;

public class MemoryConfiguration implements Configuration {
    protected JSONObject json = new JSONObject();

    public MemoryConfiguration set(String key, String value) {
        json.put(key, value);
        return this;
    }

    public MemoryConfiguration set(String key, Object value) {
        json.put(key, value);
        return this;
    }

    public MemoryConfiguration set(String key, int value) {
        json.put(key, value);
        return this;
    }
    public MemoryConfiguration set(String key, boolean value) {
        json.put(key, value);
        return this;
    }

    public MemoryConfiguration setDefault(String key, String value) {
        if (!json.has(key)) {
            json.put(key, value);
        }
        return this;
    }

    public MemoryConfiguration setDefault(String key, int value) {
        if (!json.has(key)) {
            json.put(key, value);
        }
        return this;
    }

    public MemoryConfiguration setDefault(String key, boolean value) {
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
}
