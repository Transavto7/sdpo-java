package ru.nozdratenko.sdpo.task.Alcometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

@Component
public class TranslationAlcometrTask implements Runnable {
    private RemoteEndpoint.Basic basicRemote;
    private Session session;

    @Autowired
    private AlcometerTaskRunner alcometerTaskRunner;

    public TranslationAlcometrTask setSession(Session session) {
        this.session = session;
        this.basicRemote = session.getBasicRemote();

        return this;
    }

    @Override
    public void run() {
        String now = "now";
        String status = "";
        while (session.isOpen()) {
            try {
                Thread.sleep(300);
                status = AlcometerResultTask.currentStatus.toString();
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
