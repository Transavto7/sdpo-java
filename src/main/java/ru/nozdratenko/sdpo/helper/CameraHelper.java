package ru.nozdratenko.sdpo.helper;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.util.ImageUtils;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.VideoRunException;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CameraHelper {

    public static BufferedImage makePhoto() throws IOException {
        Webcam webcam = Webcam.getDefault();

        try {
            double with = Double.valueOf(Sdpo.systemConfig.getString("camera_photo"));
            Dimension dim = getSize(with);
            webcam.setViewSize(dim);
        } catch (IllegalArgumentException e) {
            //
        }

        webcam.open();
        BufferedImage image = webcam.getImage();
        File outputfile = new File(FileBase.concatPath(FileBase.getMainFolderUrl(), "photo.jpg"));
        ImageIO.write(image, "jpg", outputfile);
        webcam.close();
        return image;
    }

    public static void makeVideo(int duration, int snaps) throws VideoRunException {
            final String filename = FileBase.concatPath(FileBase.getMainFolderUrl(), "video.mp4");
            try {
                recordScreen(filename, duration, snaps);
            } catch (AWTException | InterruptedException | IOException e) {
                throw new VideoRunException("Невозможно записать видео");
            } catch (IllegalStateException e) {
                throw new VideoRunException("Запись видео уже идет");
            }

    }

    public static String imageToBase64String(BufferedImage image) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, ImageUtils.FORMAT_JPG, os);
        return Base64.getEncoder().encodeToString(os.toByteArray());
    }

    private static void recordScreen(String filename, int duration, int snapsPerSecond)
            throws AWTException, InterruptedException, IOException {
        final Webcam webcam = Webcam.getDefault();

        try {
            double with = Double.valueOf(Sdpo.systemConfig.getString("camera_photo"));
            Dimension dim = getSize(with);
            webcam.setViewSize(dim);
        } catch (IllegalArgumentException e) {
            //
        }

        final Rectangle size = new Rectangle(webcam.getViewSize());
        final Rational framerate = Rational.make(1, snapsPerSecond);

        final Muxer muxer = Muxer.make(filename, null, null);

        final MuxerFormat format = muxer.getFormat();
        final Codec codec;
        codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

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
    }

    public static Map<Integer, Dimension> getSizes() {
        Map<Integer, Dimension> result = new HashMap<Integer, Dimension>();
        for (Dimension dim : Webcam.getDefault().getViewSizes()) {
            result.put((int) dim.getWidth(), dim);
        }

        return result;
    }

    public static Dimension getSize(Double width) {
        for (Dimension dim : Webcam.getDefault().getViewSizes()) {
            if (dim.getWidth() == width) {
                return dim;
            }
        }

        if (Webcam.getDefault().getViewSizes().length > 0) {
            return Webcam.getDefault().getViewSizes()[0];
        }

        return null;
    }

    public static String getDefaultSize() {
        double result = 0;
        for (Dimension dim : Webcam.getDefault().getViewSizes()) {
                if (result < dim.getWidth()) {
                    result = dim.getWidth();
                }
        }

        if (result == 0) {
            return null;
        }

        return "" + (int) result;
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
