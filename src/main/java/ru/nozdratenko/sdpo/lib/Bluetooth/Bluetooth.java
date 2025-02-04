package ru.nozdratenko.sdpo.lib.Bluetooth;

import java.util.HashMap;

public interface Bluetooth {
    void restart();
    String getTonometerResult(String uuid);
    String setIndicate(String uuid);
    String setConnection(String uuid);
    HashMap<String, String> scanBluetoothDevices();
}
