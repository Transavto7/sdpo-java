package ru.nozdratenko.sdpo.lib.Bluetooth;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.HashMap;

@Component
@Profile("develop")
public class MockBluetooth implements Bluetooth {
    public void restart(){
        SdpoLog.info("Bluetooth::restart");
    }

    public String getTonometerResult(String uuid)
    {
        SdpoLog.info("Bluetooth::getTonometerResult");
        return "120\\80";
    }

    public String setIndicate(String uuid)
    {
        SdpoLog.info("Bluetooth::setIndicate" + uuid);
        return "String";
    }

    public String setConnection(String uuid)
    {
        SdpoLog.info("Bluetooth::setConnection" + uuid);
        return "String";
    }

    public HashMap<String, String> scanBluetoothDevices()
    {
        SdpoLog.info("Bluetooth::scanBluetoothDevices");
        return new HashMap<>() {};
    }
}
