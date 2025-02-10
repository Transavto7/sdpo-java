package ru.nozdratenko.sdpo.task;

import org.json.JSONException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.TonometerHelper;
import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;
import ru.nozdratenko.sdpo.util.device.BluetoothDeviceService;

import java.util.List;

public class TonometerResultTask extends Thread {
    public JSONObject json = new JSONObject();
    public StatusType currentStatus = StatusType.FREE;

    @Override
    public void run() {
        Bluetooth.restart();

        while (true) {
            try {
                Thread.sleep(100);
                if (currentStatus == StatusType.REQUEST) {
                    SdpoLog.info("Start tonometer");
                    Bluetooth.restart();
                    BluetoothDeviceService.start();
                    clear();
                    this.currentStatus = StatusType.WAIT;
                }

                if (currentStatus == StatusType.STOP) {
                    SdpoLog.info("Stop tonometer");
                    Bluetooth.restart();
                    clear();
                    this.currentStatus = StatusType.FREE;
                    continue;
                }

               if (currentStatus == StatusType.WAIT) {
//                   SdpoLog.info("!!! Wait Tonometer");
                   String uuid = Sdpo.mainConfig.getString("tonometer_mac");
                   if (uuid == null || uuid.isEmpty()) {
                       continue;
                   }

                   if (TonometerHelper.scan().isEmpty()){
                       SdpoLog.info("Tonometer is disconnected");
                       continue;
                   }

                   List<String> bleappResult = BluetoothDeviceService.getTonometerResult();
                   if (bleappResult == null || bleappResult.isEmpty()) {
                       SdpoLog.info("Tonometer Result is null or empty");
                       continue;
                   }

                   String bleappLogs = bleappResult.get(0);

                   // Tonometer off
                   if (bleappLogs.startsWith("error_windows")) {
                       SdpoLog.info("Tonometer off: " + bleappLogs);
                       SdpoLog.info("set indicated...");
                       Bluetooth.setIndicate(uuid);
                       continue;
                   }

                   SdpoLog.info("Tonometer indicated");

                   if (bleappLogs.startsWith("error_")) {
                       SdpoLog.error("Tonometer error: " + bleappLogs);
                       continue;
                   }

                   if (Sdpo.systemConfig.getBoolean("tonometer_logs_visible")) {
                       SdpoLog.info("Tonometer Logs:\n" + bleappLogs);
                   }

                   String result = null;
                   if (bleappResult.size() > 1) result = bleappResult.get(1).trim();

                   if (result == null || result.isEmpty()) {
                       SdpoLog.info("Tonometer Measurement is null or empty");
                       this.currentStatus = StatusType.STOP;
//                       suspendCurrentAction(1000);
//                       continue;
                   } else {
                       if (result.contains("255")) {
                           SdpoLog.info("result contains 255");
                           continue;
                       }

                       SdpoLog.info(String.format("result: %s.", result));

                       String[] split = result.split("\\|\\|");
                       try {
                           json.put("systolic", Integer.valueOf(split[0]));
                           json.put("diastolic", Integer.valueOf(split[1]));
                           json.put("pulse", Integer.valueOf(split[2]));
                       } catch (Exception e) {
                           SdpoLog.error("Fail to create alkom result json: " + e);
                           clear();
                       }

                       this.currentStatus = StatusType.RESULT;
                   }
               }
            } catch (Exception e) {
                SdpoLog.error(e);
            }
        }
    }

    private static void suspendCurrentAction(long milsec){
        try {
            Thread.sleep(milsec);
        } catch (InterruptedException e){
            SdpoLog.error("suspendCurrentAction: " + e);
        }
    }

    public void clear() {
        json.clear();
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
