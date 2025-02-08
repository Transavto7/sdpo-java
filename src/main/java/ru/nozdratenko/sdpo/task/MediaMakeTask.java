package ru.nozdratenko.sdpo.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    private transient static final Queue<String> medias = new PriorityQueue<>();
    private static final Lock cameraLock = new ReentrantLock();
    public static transient boolean skip = false;
    @Autowired
    private CameraHelper cameraHelper;

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

        while (true) {

            if (MediaMakeTask.medias.isEmpty()) {
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    /* ignored */
                }finally {
                    continue;
                }
            }else {
                SdpoLog.info("Start media task");
                cameraHelper.openCam();
            }

            cameraLock.lock();
            String name = MediaMakeTask.medias.element();
            try {
                if (Sdpo.systemConfig.getBoolean("camera_photo")) {
                    cameraHelper.makePhoto(name);
                }

                if (Sdpo.systemConfig.getBoolean("camera_video")) {
                    cameraHelper.makeVideo(name);
                }

                MediaMakeTask.medias.remove();
            } catch (Exception e) {
                SdpoLog.error("Error recording media " + name);
                SdpoLog.error(e);
            }
            cameraLock.unlock();
        }
    }
    public static int size() {
        return medias.size();
    }
}

