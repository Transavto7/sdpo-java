package ru.nozdratenko.sdpo.commands;


import com.github.sarxos.webcam.Webcam;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.task.AlcometerResultTask;
import ru.nozdratenko.sdpo.task.MediaMakeTask;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoCommand extends Command {

    @Override
    String getCommand() {
        return "f-photo";
    }

    @Override
    void run(String[] args) {
        int count = 1;
        int sleep = 2000;

        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }

        if (args.length > 1) {
            sleep = Integer.parseInt(args[1]);
        }

        CameraHelper.openCam();

        for (int i = 1; i <= count; i++) {
            SdpoLog.info("------ Test " + i);
            try {
                this.testPhoto(i);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        CameraHelper.closeCam();
    }

    private void testPhoto(int number) throws InterruptedException {
        SdpoLog.info("Make photo...");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_k-m-s-S");
        String name = "test_" + number + "_" + simpleDateFormat.format(new Date()) + ".png";
        SdpoLog.info("name: " + name);
        try {
            CameraHelper.makePhoto(name);
        } catch (Exception e) {
            SdpoLog.error("Error record media " + name);
            SdpoLog.error(e);
        }
    }
}
