package ru.nozdratenko.sdpo.task;

import org.json.JSONException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;

public class TonometerResultTask extends Thread {
    public JSONObject json = new JSONObject();

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2000);

                String uuid = Sdpo.mainConfig.getString("tonometer_mac");
                if (uuid == null || uuid.isEmpty()) {
                    continue;
                }

                String result = Bluetooth.getTonometerResult(uuid);

                if (result == null || result.isEmpty()) {
                    continue;
                }

                SdpoLog.info("result: " + result);

                if (result.startsWith("error_")) {
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
