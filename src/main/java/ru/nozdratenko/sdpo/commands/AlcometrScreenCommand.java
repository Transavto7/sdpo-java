package ru.nozdratenko.sdpo.commands;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.helper.CameraHelpers.WindowsCameraHelper;
import ru.nozdratenko.sdpo.task.Alcometer.AlcometerResultTask;
import ru.nozdratenko.sdpo.task.Alcometer.AlcometerTaskRunner;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

@Component
public class AlcometrScreenCommand extends Command {
    @Autowired
    private AlcometerTaskRunner alcometerTaskRunner;
    @Autowired
    private CameraHelper cameraHelper;

    @Override
    String getCommand() {
        return "u-alco";
    }

    @Override
    void run(String[] args) {
        int count = 1;
        int sleep = 2000;
        boolean fast = true;

        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }

        if (args.length > 1) {
            sleep = Integer.parseInt(args[1]);
        }

        if (args.length > 2) {
            fast = Boolean.parseBoolean(args[2]);
        }

        SdpoLog.info("Await start alcometr...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SdpoLog.info("Alcometr fast mode: " + fast);
        Sdpo.settings.systemConfig.set("alcometer_fast", fast);

        for (int i = 1; i <= count; i++) {
            SdpoLog.info("------ Test " + i);
            try {
                this.testAlcometr();
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void testAlcometr() throws InterruptedException {
        SdpoLog.info("Make media...");
        cameraHelper.makePhotoAndVideo();

        SdpoLog.info("Run alcometr...");

        while (true) {
            if (AlcometerResultTask.currentStatus == StatusType.FREE) {
                AlcometerResultTask.currentStatus = StatusType.REQUEST;
            } else if (AlcometerResultTask.currentStatus == StatusType.ERROR) {
                SdpoLog.error(AlcometerResultTask.error.toString());
                AlcometerResultTask.currentStatus = StatusType.STOP;
                break;
            } else if (AlcometerResultTask.currentStatus == StatusType.RESULT) {
                AlcometerResultTask.currentStatus = StatusType.STOP;
                SdpoLog.info("Alcometr result " + AlcometerResultTask.result);
                break;
            }

            Thread.sleep(20);
        }
    }
}
