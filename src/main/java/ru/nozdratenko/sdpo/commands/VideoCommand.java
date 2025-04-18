package ru.nozdratenko.sdpo.commands;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.helper.CameraHelpers.WindowsCameraHelper;
import ru.nozdratenko.sdpo.task.Alcometer.AlcometerTaskRunner;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class VideoCommand extends Command {
    @Autowired
    private CameraHelper cameraHelper;

    @Override
    String getCommand() {
        return "f-video";
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

        cameraHelper.openCam();

        for (int i = 1; i <= count; i++) {
            SdpoLog.info("------ Test " + i);
            try {
                this.testVideo(i);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void testVideo(int number) throws InterruptedException {
        SdpoLog.info("Make video...");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_k-m-s-S");
        String name = "test_" + number + "_" + simpleDateFormat.format(new Date());
        SdpoLog.info("name: " + name);
        try {
            cameraHelper.makeVideo(name);
        } catch (Exception e) {
            SdpoLog.error("Error record media " + name);
            SdpoLog.error(e);
        }
    }
}
