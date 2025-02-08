package ru.nozdratenko.sdpo.lib;

import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.HashMap;

public class Bluetooth {
    static {
        try {
            String url = FileBase.exportLibrary("blecpp.dll");
            System.load(url.replace("\\", "/"));
            SdpoLog.info("blecpp.dll loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            SdpoLog.error("Error loading DLL: " + e.getMessage());
            throw e;
        }
    }

    public static native void restart();
    public static native String getTonometerResult(String uuid);
    public static native String setIndicate(String uuid);
    public static native String setConnection(String uuid);
    public static native HashMap<String, String> scanBluetoothDevices();
}
