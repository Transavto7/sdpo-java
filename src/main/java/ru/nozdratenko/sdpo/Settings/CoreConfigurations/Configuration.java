package ru.nozdratenko.sdpo.Settings.CoreConfigurations;

import org.json.JSONObject;

public interface Configuration {

    Configuration set(String key, String value);

    Configuration set(String key, Object value);

    Configuration set(String key, int value);

    Configuration set(String key, boolean value);

    Configuration setDefault(String key, String value);

    Configuration setDefault(String key, int value);

    Configuration setDefault(String key, boolean value);

    String getString(String key);

    int getInt(String key);

    boolean getBoolean(String key);

    JSONObject getJson();
}
