package ru.nozdratenko.sdpo.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.task.BluetoothUpdateTask;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import java.io.IOException;

public class TonometerHelper {


    public static JSONObject scan() {
        JSONObject json = new JSONObject();
        try {
            LocalDevice dev = LocalDevice.getLocalDevice();

            JSONObject mainDevice = new JSONObject();
            mainDevice.put("name", dev.getFriendlyName());
            mainDevice.put("address", dev.getBluetoothAddress());
            json.put("main", mainDevice);

            JSONArray devices = new JSONArray();
            for (String address : BluetoothUpdateTask.devices.keySet()) {
                JSONObject deviceData = new JSONObject();
                String name = BluetoothUpdateTask.devices.get(address);

                deviceData.put("name", name);
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
