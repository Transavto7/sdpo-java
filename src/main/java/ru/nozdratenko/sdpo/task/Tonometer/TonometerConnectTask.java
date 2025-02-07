package ru.nozdratenko.sdpo.task.Tonometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.lib.Bluetooth.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

@Component
public class TonometerConnectTask implements Runnable {
    private final Bluetooth bluetooth;

    public StatusType currentStatus = StatusType.FREE;

    @Autowired
    public TonometerConnectTask(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                break;
            }

            if (currentStatus.skip) {
                continue;
            }

            String uuid = Sdpo.mainConfig.getString("tonometer_mac");

            if (uuid == null || uuid.isEmpty()) {
                continue;
            }

            if (currentStatus == StatusType.STOP) {
                this.currentStatus = StatusType.FREE;
            }

            if (currentStatus == StatusType.WAIT) {
                String result = bluetooth.setConnection(uuid);

                if (result.equals("set")) {
                    bluetooth.setIndicate(uuid);
                    currentStatus = StatusType.RESULT;
//                    Sdpo.mainConfig.set("tonometer_connect", true);
//                    Sdpo.mainConfig.saveFile();
                }
            }
        }
    }
}