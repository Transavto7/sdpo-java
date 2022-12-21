package ru.nozdratenko.sdpo.lib;

public class Bluetooth {

    static {
        System.load("C:/Users/ellidey/AppData/Roaming/sdpo/native/blecpp.dll");
    }
    public static native String findDevice();
    public static native String getTonometerResult(String uuid);
}
