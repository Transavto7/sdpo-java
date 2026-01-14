package ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Service
@Profile("production")
public class WindowsEnvironmentMonitorHelper implements EnvironmentMonitorHelper {

    private boolean isStarted = false;
    private EnvironmentMonitor monitor;

    @Override
    public void init() {
        if (isStarted) {
            destroy();
        }
        monitor = new SerialEnvironmentMonitor();
        isStarted = true;

        try {
            monitor.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SensorData getEnvironmentData() {
        try {
            return monitor.getAllData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void destroy() {
        try {
            monitor.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
