package ru.nozdratenko.sdpo.task;

import com.github.sarxos.webcam.Webcam;
import com.xuggle.xuggler.video.ConverterFactory;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.imageio.ImageIO;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
        Webcam webcam = Webcam.getDefault();
        while (true) {
            BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
            if (!session.isOpen()) {
                SdpoLog.info("Close translation video");
                return;
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image,"jpg", baos);
                byte[] byteArray = baos.toByteArray();
                basicRemote.sendObject(byteArray);
            } catch (IOException | EncodeException | IllegalArgumentException | IllegalStateException e) {
                SdpoLog.error("Failed serializable foto!");
            }
        }

    }
}
