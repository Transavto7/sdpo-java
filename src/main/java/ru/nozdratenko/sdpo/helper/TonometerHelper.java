package ru.nozdratenko.sdpo.helper;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;

public class TonometerHelper {

    public static JSONObject scan() {
        JSONObject json = new JSONObject();
        try {
            LocalDevice dev = LocalDevice.getLocalDevice();
            RemoteDevice[] remoteDevices = dev.getDiscoveryAgent().retrieveDevices(0);

            JSONObject mainDevice = new JSONObject();
            mainDevice.put("name", dev.getFriendlyName());
            mainDevice.put("address", dev.getBluetoothAddress());
            json.put("main", mainDevice);

            if (remoteDevices == null) {
                return json;
            }

            JSONArray devices = new JSONArray();
            for (RemoteDevice device : remoteDevices) {
                JSONObject deviceData = new JSONObject();
                deviceData.put("name", device.getFriendlyName(false));
                deviceData.put("address", device.getBluetoothAddress());
                devices.put(deviceData);
            }
            json.put("devices", devices);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String connect(String address) throws IOException {
        System.setProperty("bluecove.stack", "winsock");
        String SERVICE_URL = "btspp://3414B58F834E:1";

        System.out.println("Server is waiting for client ... \n URL=" + SERVICE_URL);
         Connector.open(SERVICE_URL);

        System.out.println("connected");
//        StreamConnection connection = connectionNotifier.acceptAndOpen();
//        RemoteDevice remoteDevice = RemoteDevice.getRemoteDevice(connection);
//        return remoteDevice.getBluetoothAddress();
        return "";
    }

}
