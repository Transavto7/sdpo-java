package ru.nozdratenko.sdpo.lib.BluetoothServices;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.lib.Bluetooth;

import java.util.HashMap;

@Component
@Profile("production")
public class WindowsBluetooth implements ru.nozdratenko.sdpo.lib.BluetoothServices.Bluetooth {

    @Override
    public void restart()
    {
        Bluetooth.restart();
    }

    @Override
    public String getTonometerResult(String uuid)
    {
        return Bluetooth.getTonometerResult(uuid);
    }

    @Override
    public String setIndicate(String uuid)
    {
        return Bluetooth.setIndicate(uuid);
    }

    @Override
    public String setConnection(String uuid)
    {
        return Bluetooth.setConnection(uuid);
    }

    @Override
    public HashMap<String, String> scanBluetoothDevices()
    {
        return Bluetooth.scanBluetoothDevices();
    }
}
