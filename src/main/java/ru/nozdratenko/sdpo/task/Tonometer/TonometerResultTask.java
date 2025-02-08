package ru.nozdratenko.sdpo.task.Tonometer;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.lib.BluetoothServices.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;
import ru.nozdratenko.sdpo.util.device.BluetoothDeviceService;

@Component
public class TonometerResultTask implements Runnable {
    private final JSONObject json = new JSONObject();
    private StatusType currentStatus = StatusType.FREE;
    private final Bluetooth bluetooth;

    @Autowired
    public TonometerResultTask(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }


    @Override
    public void run() {
        this.bluetooth.restart();
        BluetoothDeviceService.start();

        while (true) {
            try {
                Thread.sleep(100);
                if (currentStatus == StatusType.REQUEST) {
                    SdpoLog.info("Start tonometer");
                    this.bluetooth.restart();
                    this.json.clear();
                    this.currentStatus = StatusType.WAIT;
                }

                if (currentStatus == StatusType.STOP) {
                    SdpoLog.info("Stop tonometer");
                    this.bluetooth.restart();
                    this.json.clear();
                    this.currentStatus = StatusType.FREE;
                    continue;
                }

               if (currentStatus == StatusType.WAIT) {
                   String uuid = Sdpo.mainConfig.getString("tonometer_mac");
                   if (uuid == null || uuid.isEmpty()) {
                       continue;
                   }

                    String result = BluetoothDeviceService.getTonometerResult();

                   if (result == null || result.isEmpty()) {
                       continue;
                   }

                   // Tonometer off
                   if (result.startsWith("error_windows")) {
                       SdpoLog.info("result: " + result);
                       SdpoLog.info("set indicated...");
                       this.bluetooth.setIndicate(uuid);
                       continue;
                   }

                   SdpoLog.info("Tonometer indicated");

                   if (result.startsWith("error_")) {
                       SdpoLog.error("Tonometer error code: " + result);
                       continue;
                   }

                   SdpoLog.info("result: " + result);

                   if (result.contains("255")) {
                       continue;
                   }

                   String[] split = result.split("\\|\\|");
                   try {
                       json.put("systolic", Integer.valueOf(split[0]));
                       json.put("diastolic", Integer.valueOf(split[1]));
                       json.put("pulse", Integer.valueOf(split[2]));
                   } catch (Exception e) {
                       json.clear();
                   }

                   this.currentStatus = StatusType.RESULT;
               }
            } catch (Exception e) {
                SdpoLog.error(e);
            }
        }
    }

    public void clear() {
        json.clear();
    }

    public JSONObject getJson() {
        return this.json;
    }

    public StatusType getCurrentStatus() {
        return this.currentStatus;
    }

    public void setCurrentStatus(StatusType status) {
        this.currentStatus = status;
    }

    public int getSystolic() {
        try {
            return json.getInt("systolic");
        } catch (JSONException e) {
            return 0;
        }
    }

    public int getDiastolic() {
        try {
            return json.getInt("diastolic");
        } catch (JSONException e) {
            return 0;
        }
    }

    public int getPulse() {
        try {
            return json.getInt("pulse");
        } catch (JSONException e) {
            return 0;
        }
    }
}
