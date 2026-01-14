package ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.time.LocalDateTime;

@Service
@Profile("develop")
public class MockEnvironmentMonitorHelper implements EnvironmentMonitorHelper {
    @Override
    public void init() {
        SdpoLog.info("MockEnvironmentMonitorHelper::init");
    }

    @Override
    public SensorData getEnvironmentData() {
        SensorData sensorData = new SensorData();
        sensorData.setDeviceId(1);
        sensorData.setTemperature(24.0f);
        sensorData.setPressure(100197f);
        sensorData.setHumidity(28.0f);
        sensorData.setIlluminance(34f);
        sensorData.setTamperDetected(false);
        sensorData.setTimestamp(LocalDateTime.now());

        return sensorData;
    }

    @Override
    public void destroy() {
        SdpoLog.info("MockEnvironmentMonitorHelper::destroy");
    }
}
