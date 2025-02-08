package ru.nozdratenko.sdpo.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.lib.BluetoothServices.Bluetooth;

import java.util.HashMap;

@Service
public class TonometerHelper {
    private final Bluetooth bluetooth;

    @Autowired
    public TonometerHelper(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }

    public JSONObject scan() {
        JSONObject json = new JSONObject();
        try {
            HashMap<String, String> devicesMap = this.bluetooth.scanBluetoothDevices();

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
