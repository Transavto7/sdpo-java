package ru.nozdratenko.sdpo.lib.Bluetooth;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.file.FileBase;

import java.util.HashMap;

@Component
@Profile("production")
public class WindowsBluetooth implements Bluetooth {

    public WindowsBluetooth() {
        String url = FileBase.exportLibrary("blecpp.dll");
        System.load(url.replace("\\", "/"));
    }

    @Override
    public native void restart();

    @Override
    public native String getTonometerResult(String uuid);

    @Override
    public native String setIndicate(String uuid);

    @Override
    public native String setConnection(String uuid);

    @Override
    public native HashMap<String, String> scanBluetoothDevices();
}
