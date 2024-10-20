package ru.nozdratenko.sdpo.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.HashMap;

public class TonometerHelper {

    public static JSONObject scan() {
        JSONObject json = new JSONObject();
        try {
            HashMap<String, String> devicesMap = Bluetooth.scanBluetoothDevices();

            JSONArray devices = new JSONArray();
            for (String address : devicesMap.keySet()) {
                JSONObject deviceData = new JSONObject();
                deviceData.put("address", address);
                deviceData.put("name", devicesMap.get(address));
                devices.put(deviceData);
            }

            json.put("devices", devices);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
