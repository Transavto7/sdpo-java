package ru.nozdratenko.sdpo.websocket;

import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Core.Framework.SpringContext;
import ru.nozdratenko.sdpo.task.TranslationVideoTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.websocket.MessageHandler;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/video")
@Component
public class VideoEndpoint implements MessageHandler {
    private Thread translationTask;
    public static List<Session> sessionList = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        TranslationVideoTask translationTaskRunnable = SpringContext.getBean(TranslationVideoTask.class);

        sessionList.add(session);
        if (translationTask == null || !translationTask.isAlive()) {
            translationTask = new Thread(translationTaskRunnable.setSession(session));
            translationTask.start();
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
