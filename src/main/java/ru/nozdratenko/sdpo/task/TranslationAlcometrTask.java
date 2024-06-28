package ru.nozdratenko.sdpo.task;

import org.springframework.http.ResponseEntity;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

public class TranslationAlcometrTask extends Thread {
    private final RemoteEndpoint.Basic basicRemote;
    private final Session session;

    public TranslationAlcometrTask(Session session) {
        this.basicRemote = session.getBasicRemote();
        this.session = session;
    }

    @Override
    public void run() {
        String now = "now";
        String status = "";
        while (session.isOpen()) {
            try {
                Thread.sleep(300);
                status = Sdpo.alcometerResultTask.currentStatus.toString();
                if (!now.equals(status)) {
                    now = status;
                    basicRemote.sendText(now);
                }
            } catch (IOException | IllegalArgumentException | IllegalStateException e) {
                SdpoLog.error("Failed send status from alcometr!");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        SdpoLog.info("Close translation status from alcometr");

    }
}
