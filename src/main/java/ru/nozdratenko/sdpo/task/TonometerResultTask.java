package ru.nozdratenko.sdpo.task;

import org.json.JSONException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

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
                    this.json.clear();
                    this.currentStatus = StatusType.WAIT;
                }

                if (currentStatus == StatusType.STOP) {
                    SdpoLog.info("Stop tonometer");
                    Bluetooth.restart();
                    this.json.clear();
                    this.currentStatus = StatusType.FREE;
                    continue;
                }

               if (currentStatus == StatusType.WAIT) {
                   String uuid = Sdpo.mainConfig.getString("tonometer_mac");
                   if (uuid == null || uuid.isEmpty()) {
                       continue;
                   }


                   String result = Bluetooth.getTonometerResult(uuid);
                   SdpoLog.info(uuid + " Tonometer Result: " + result);

                   if (result == null || result.isEmpty()) {
                       continue;
                   }

                   // Tonometer off
                   if (result.equals("error_windows")) {
                       SdpoLog.info("set indicated...");
                       Bluetooth.setIndicate(uuid);
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
