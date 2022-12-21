package ru.nozdratenko.sdpo.task;

import ru.nozdratenko.sdpo.lib.Bluetooth;

import java.util.HashMap;
import java.util.Map;

public class BluetoothUpdateTask extends Thread {
    public static Map<String, String> devices = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            String str = Bluetooth.findDevice();
            String[] split = str.split("\\|\\|");
            String address = split[0];
            String name = "Неизвестное устройство";

            if (split.length > 1) {
                name = split[1];
            }

            devices.put(address, name);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
