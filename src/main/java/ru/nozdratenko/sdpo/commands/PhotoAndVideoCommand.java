package ru.nozdratenko.sdpo.commands;


import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.task.MediaMakeTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

public class PhotoAndVideoCommand extends Command {

    @Override
    String getCommand() {
        return "u-media";
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

        for (int i = 1; i <= count; i++) {
            SdpoLog.info("------ Test " + i);
            try {
                this.testMedia(i);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (true) {
            if (MediaMakeTask.size() == 0) {
                SdpoLog.info("All media saved!");
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void testMedia(int number) throws InterruptedException {
        SdpoLog.info("Make media " + number + "...");
        CameraHelper.makePhotoAndVideo();
    }
}
