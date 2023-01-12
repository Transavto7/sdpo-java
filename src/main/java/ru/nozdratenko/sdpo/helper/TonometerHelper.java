package ru.nozdratenko.sdpo.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.task.BluetoothUpdateTask;

import javax.bluetooth.*;

public class TonometerHelper {

    public static JSONObject scan() {
        JSONObject json = new JSONObject();
        try {
            LocalDevice dev = LocalDevice.getLocalDevice();
            new BluetoothUpdateTask().start();

            JSONObject mainDevice = new JSONObject();
            mainDevice.put("name", dev.getFriendlyName());
            mainDevice.put("address", dev.getBluetoothAddress());
            json.put("main", mainDevice);

            JSONArray devices = new JSONArray();
            for (String address : BluetoothUpdateTask.devices) {
                JSONObject deviceData = new JSONObject();
                deviceData.put("address", address);
                devices.put(deviceData);
            }

            json.put("devices", devices);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }
}
