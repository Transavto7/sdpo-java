package ru.nozdratenko.sdpo.task;

import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUpdateTask extends Thread {
    public static List<String> devices = new ArrayList<>();
    public static boolean locked = false;

    @Override
    public void run() {
        if (locked) {
            return;
        }

        locked = true;
        SdpoLog.info("scan devices...");
        String str = Bluetooth.findDevice();
        str = str.trim();

        SdpoLog.error(str);
        if (str.startsWith("error_")) {
            SdpoLog.error(str);
        } else {
            if (!devices.contains(str)) {
                devices.add(str);
            }
        }

        locked = false;
    }
}
