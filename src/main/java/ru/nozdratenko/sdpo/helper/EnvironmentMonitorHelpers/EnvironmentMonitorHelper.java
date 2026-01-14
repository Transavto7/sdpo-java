package ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers;

public interface EnvironmentMonitorHelper {
    void init();
    SensorData getEnvironmentData();
    void destroy();
}
