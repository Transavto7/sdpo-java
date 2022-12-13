package ru.nozdratenko.sdpo.helper;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.util.ImageUtils;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import ru.nozdratenko.sdpo.exception.VideoRunException;
import ru.nozdratenko.sdpo.file.FileBase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class CameraHelper {
    public static Thread videoThread = null;

    public static BufferedImage makePhoto() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        webcam.close();
        return image;
    }

    public static void makeVideo(int duration, int snaps) throws VideoRunException {
        if (videoThread != null && videoThread.isAlive()) {
            throw new VideoRunException("video record is run");
        }
            final String codecname = null;
            final String formatname = null;
            final String filename = FileBase.concatPath(FileBase.getMainFolderUrl(), "video.mp4");

            try {
                recordScreen(filename, formatname, codecname, duration, snaps);
            } catch (AWTException | InterruptedException | IOException e) {
                e.printStackTrace();
            }

    }

    public static String imageToBase64String(BufferedImage image) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, ImageUtils.FORMAT_JPG, os);
        return Base64.getEncoder().encodeToString(os.toByteArray());
    }

    private static void recordScreen(String filename, String formatname, String codecname, int duration, int snapsPerSecond)
            throws AWTException, InterruptedException, IOException {
        final Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        final Rectangle size = new Rectangle(webcam.getViewSize());
        final Rational framerate = Rational.make(1, snapsPerSecond);

        final Muxer muxer = Muxer.make(filename, null, formatname);

        final MuxerFormat format = muxer.getFormat();
        final Codec codec;
        if (codecname != null) {
            codec = Codec.findEncodingCodecByName(codecname);
        } else {
            codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
        }

        Encoder encoder = Encoder.make(codec);

        encoder.setWidth(size.width);
        encoder.setHeight(size.height);

        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate);

        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);

        webcam.open();

        videoThread = new Thread(() -> {
            MediaPictureConverter converter = null;
            final MediaPicture picture = MediaPicture
                    .make(encoder.getWidth(),
                            encoder.getHeight(),
                            pixelformat);
            picture.setTimeBase(framerate);

            final MediaPacket packet = MediaPacket.make();
            for (int i = 0; i < duration / framerate.getDouble(); i++) {
                final BufferedImage image = webcam.getImage();
                final BufferedImage frame = convertToType(image, BufferedImage.TYPE_3BYTE_BGR);

                if (converter == null) {
                    converter = MediaPictureConverterFactory.createConverter(frame, picture);
                }
                converter.toPicture(picture, frame, i);

                do {
                    encoder.encode(packet, picture);
                    if (packet.isComplete()) {
                        muxer.write(packet, false);
                    }
                } while (packet.isComplete());

                try {
                    Thread.sleep((long) (1000 * framerate.getDouble()));
                } catch (InterruptedException e) { }
            }

            do {
                encoder.encode(packet, null);
                if (packet.isComplete()) {
                    muxer.write(packet, false);
                }
            } while (packet.isComplete());

            webcam.close();
            muxer.close();
        });
        videoThread.start();
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType)
            image = sourceImage;
        else {
            image = new BufferedImage(
                    sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

    public static byte[] readVideoByte() throws IOException {

        FileInputStream inputStream = new FileInputStream(FileBase.concatPath(FileBase.getMainFolderUrl(), "video.mp4"));
        ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bufferedOutputStream.write(data, 0, nRead);
        }
        bufferedOutputStream.flush();
        return bufferedOutputStream.toByteArray();
    }
}
