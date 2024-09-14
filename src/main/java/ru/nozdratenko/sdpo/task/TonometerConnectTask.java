package ru.nozdratenko.sdpo.task;

import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

public class TonometerConnectTask extends Thread {
    public StatusType currentStatus = StatusType.FREE;

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                break;
            }

            if (currentStatus.skip) {
                continue;
            }

            String uuid = Sdpo.mainConfig.getString("tonometer_mac");
            SdpoLog.info("!!! UUID: " + uuid);
            if (uuid == null || uuid.isEmpty()) {
                continue;
            }

            if (currentStatus == StatusType.STOP) {
                this.currentStatus = StatusType.FREE;
            }

            if (currentStatus == StatusType.WAIT) {
                SdpoLog.info("TonometerConnectTask|StatusType.WAIT|Bluetooth.setIndicate: " + uuid);
                String result = Bluetooth.setConnection(uuid);

                if (result.equals("set")) {
                    SdpoLog.info("TonometerConnectTask|StatusType.WAIT|Bluetooth.setIndicate: " + uuid);
                    Bluetooth.setIndicate(uuid);
                    currentStatus = StatusType.RESULT;
//                    Sdpo.mainConfig.set("tonometer_connect", true);
//                    Sdpo.mainConfig.saveFile();
                }
            }
        }
    }
}
