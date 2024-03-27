package ru.nozdratenko.sdpo.task;

import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

public class TranslationVideoTask extends Thread {
    private final RemoteEndpoint.Basic basicRemote;
    private final Session session;

    public TranslationVideoTask(Session session) {
        this.basicRemote = session.getBasicRemote();
        this.session = session;
    }

    @Override
    public void run() {
        CameraHelper.openCam();
        while (session.isOpen()) {
            try {
                basicRemote.sendObject(CameraHelper.makePhotoBytes());
            } catch (IOException | EncodeException | IllegalArgumentException | IllegalStateException e) {
                SdpoLog.error("Failed serializable foto!");
            }
        }
        SdpoLog.info("Close translation video");

    }
}
