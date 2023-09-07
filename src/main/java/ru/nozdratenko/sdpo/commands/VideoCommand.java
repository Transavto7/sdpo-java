package ru.nozdratenko.sdpo.commands;


import com.github.sarxos.webcam.Webcam;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoCommand extends Command {

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

        CameraHelper.openCam();

        for (int i = 1; i <= count; i++) {
            SdpoLog.info("------ Test " + i);
            try {
                this.testVideo(i);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        CameraHelper.closeCam();
    }

    private void testVideo(int number) throws InterruptedException {
        SdpoLog.info("Make video...");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_k-m-s-S");
        String name = "test_" + number + "_" + simpleDateFormat.format(new Date());
        SdpoLog.info("name: " + name);
        try {
            CameraHelper.makeVideo(name);
        } catch (Exception e) {
            SdpoLog.error("Error record media " + name);
            SdpoLog.error(e);
        }
    }
}
