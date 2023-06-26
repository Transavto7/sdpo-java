package ru.nozdratenko.sdpo.task;

import com.github.sarxos.webcam.Webcam;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.VideoRunException;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

public class MediaMakeTask extends Thread {
    private static Queue<String> medias = new PriorityQueue<>();

    public static void record(String name) {
        medias.offer(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                /* ignored */
            }

            if (MediaMakeTask.medias.size() < 1) {
                continue;
            }

            CameraHelper.openCam();

            SdpoLog.info("count: " + MediaMakeTask.medias.size());
            while (MediaMakeTask.medias.size() > 0) {
                String name = MediaMakeTask.medias.poll();
                SdpoLog.info("name: " + name);
                try {
                    if (Sdpo.systemConfig.getBoolean("camera_photo")) {
                        CameraHelper.makePhoto(name);
                    }

                    if (Sdpo.systemConfig.getBoolean("camera_photo")) {
                        CameraHelper.makeVideo(name);
                    }
                } catch (Exception e) {
                    SdpoLog.error("Error record media " + name);
                    SdpoLog.error(e);
                }
            }
        }
    }

    public static int size() {
        return medias.size();
    }
}
