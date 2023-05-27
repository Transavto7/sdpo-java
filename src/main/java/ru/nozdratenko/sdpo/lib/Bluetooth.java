package ru.nozdratenko.sdpo.lib;

import ru.nozdratenko.sdpo.file.FileBase;

import java.util.HashMap;

public class Bluetooth {

    static {
        String url = FileBase.exportLibrary("blecpp.dll");
        System.load(url.replace("\\", "/"));
    }

    public static native void restart();
    public static native String getTonometerResult(String uuid);
    public static native String setIndicate(String uuid);
    public static native String setConnection(String uuid);
    public static native HashMap<String, String> scanBluetoothDevices();
}
