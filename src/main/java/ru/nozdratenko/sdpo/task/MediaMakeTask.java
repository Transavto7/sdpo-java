package ru.nozdratenko.sdpo.task;

import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MediaMakeTask extends Thread {
    private static final Queue<String> medias = new PriorityQueue<>();
    private static final Lock cameraLock = new ReentrantLock();

    public static void record(String name) {
        SdpoLog.info(medias.size() + " add " + name);
        medias.offer(name);
    }

    @Override
    public void run() {
        CameraHelper.openCam();

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                /* ignored */
            }

            if (MediaMakeTask.medias.isEmpty()) {
                continue;
            }

            cameraLock.lock();
            while (!MediaMakeTask.medias.isEmpty()) {
                String name = MediaMakeTask.medias.element();
                try {
                    if (Sdpo.systemConfig.getBoolean("camera_photo")) {
                        CameraHelper.makePhoto(name);
                    }

                    if (Sdpo.systemConfig.getBoolean("camera_video")) {
                        CameraHelper.makeVideo(name);
                    }

                    MediaMakeTask.medias.remove();
                } catch (Exception e) {
                    SdpoLog.error("Error recording media " + name);
                    SdpoLog.error(e);
                }
            }

            cameraLock.unlock();
        }
    }
    public static int size() {
        return medias.size();
    }
}

