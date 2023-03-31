package ru.nozdratenko.sdpo.websocket;

import com.github.sarxos.webcam.Webcam;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.task.TranslationVideoTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/video")
@Component
public class VideoEndpoint implements MessageHandler {
    private TranslationVideoTask translationTask;
    public static List<Session> sessionList = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessionList.add(session);

        if (Webcam.getDefault().isOpen()) {
            SdpoLog.error("cant run translation video. Webcam is open");
        }

        if (translationTask == null || !translationTask.isAlive()) {
            translationTask = new TranslationVideoTask(session);
            translationTask.run();
            SdpoLog.info("Translation video run");
        } else {
            SdpoLog.warning("Translation video is running");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        SdpoLog.error(throwable);
    }
}
