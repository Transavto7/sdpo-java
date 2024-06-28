package ru.nozdratenko.sdpo.websocket;

import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.task.TranslationAlcometrTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.websocket.MessageHandler;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/device/alcometer/status")
@Component
public class AlcometrStatusEndPoint implements MessageHandler {
    private TranslationAlcometrTask translationTask;
    public static List<Session> sessionList = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessionList.add(session);
        if (translationTask == null || !translationTask.isAlive()) {
            translationTask = new TranslationAlcometrTask(session);
            translationTask.start();
            SdpoLog.info("Translation status from alcometr run");
        } else {
            SdpoLog.warning("Translation status from alcometr is running");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        SdpoLog.error(throwable);
    }
}
