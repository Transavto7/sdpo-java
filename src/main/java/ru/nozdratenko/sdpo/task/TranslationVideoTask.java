package ru.nozdratenko.sdpo.task;

import com.github.sarxos.webcam.Webcam;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.imageio.ImageIO;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

public class TranslationVideoTask extends Thread {
    private final RemoteEndpoint.Basic basicRemote;
    private final Session session;
    private boolean isAlive = true;

    public TranslationVideoTask(Session session) {
        this.basicRemote = session.getBasicRemote();
        this.session = session;
    }

    @Override
    public void run() {
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        while (true) {
            if (!isAlive) {
                SdpoLog.info("Close translation video task");
                return;
            }

            BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);

            if (!session.isOpen()) {
                webcam.close();
                SdpoLog.info("Close translation video");
                return;
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image,"jpg", baos);
                byte[] byteArray = baos.toByteArray();
                basicRemote.sendObject(byteArray);
            } catch (IOException | EncodeException | IllegalArgumentException | IllegalStateException e) {
                /* ignored */
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                /* ignored */
            }
        }

    }
}
