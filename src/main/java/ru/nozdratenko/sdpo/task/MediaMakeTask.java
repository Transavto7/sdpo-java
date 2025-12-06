package ru.nozdratenko.sdpo.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.helper.CameraHelpers.WindowsCameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MediaMakeTask extends Thread {
    private static final Queue<String> medias = new PriorityQueue<>();
    private static final Lock cameraLock = new ReentrantLock();
    public static boolean skip = false;
    private volatile boolean running = true;

    public void shutdown() {
        running = false;
        this.interrupt();
    }

    public static void record(String name) {
        medias.offer(name);
        SdpoLog.info(String.format("Queue size: %s added new thread media task, name: %s", medias.size(), name));
    }

    public static void mediaLastKill() {
        SdpoLog.info("Skip other frame first video. Kill video task!");
        MediaMakeTask.skip = true;
    }

    @Override
    public void run() {
        CameraHelper cameraHelper = SpringContext.getBean(CameraHelper.class);
        SdpoLog.info("MediaMakeTask started.");

        while (running) {

            if (medias.isEmpty()) {
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    if (!running) {
                        break;
                    }
                }
                continue;
            }

            SdpoLog.info("Start media task");
            cameraHelper.openCam();

            cameraLock.lock();
            try {
                String name = medias.element();

                if (Sdpo.settings.systemConfig.getBoolean("camera_photo")) {
                    cameraHelper.makePhoto(name);
                }

                if (Sdpo.settings.systemConfig.getBoolean("camera_video")) {
                    cameraHelper.makeVideo(name);
                }

                medias.remove();

            } catch (Exception e) {
                SdpoLog.error("Error recording media");
                SdpoLog.error(e);
            } finally {
                cameraLock.unlock();
            }
        }

        SdpoLog.info("MediaMakeTask stopped.");
    }


    public static int size() {
        return medias.size();
    }
}

