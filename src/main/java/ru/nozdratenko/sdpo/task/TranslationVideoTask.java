package ru.nozdratenko.sdpo.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.helper.CameraHelpers.WindowsCameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

@Component
public class TranslationVideoTask implements Runnable {
    @Autowired
    private CameraHelper cameraHelper;

    private RemoteEndpoint.Basic basicRemote;
    private Session session;

    public TranslationVideoTask setSession(Session session) {
        this.basicRemote = session.getBasicRemote();
        this.session = session;

        return this;
    }

    @Override
    public void run() {
        cameraHelper.openCam();
        while (session.isOpen()) {
            try {
                basicRemote.sendObject(cameraHelper.makePhotoBytes());
            } catch (IOException | EncodeException | IllegalArgumentException | IllegalStateException e) {
                SdpoLog.error("Failed serializable foto!");
            }
        }
        SdpoLog.info("Close translation video");
    }
}
