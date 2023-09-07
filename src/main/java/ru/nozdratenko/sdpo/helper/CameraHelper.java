package ru.nozdratenko.sdpo.helper;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.network.MultipartUtility;
import ru.nozdratenko.sdpo.task.MediaMakeTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CameraHelper {
    public static File lastResultVideo = null;
    public static File getLastResultPhoto = null;

    public static boolean isCameraAvailable() {
        try {
            return Webcam.getDefault() != null;
        } catch (WebcamException e) {
            SdpoLog.warning("No webcam has been detected!");
            return false;
        }
    }
    public static void initDimension() {
        try {
            CameraHelper.closeCam();
            Webcam webcam = Webcam.getDefault();
            double with = Double.parseDouble(Sdpo.systemConfig.getString("camera_dimension"));
            Dimension dim = getSize(with);
            webcam.setViewSize(dim);
        } catch (IllegalArgumentException e) {
            SdpoLog.warning(e);
        } finally {
            CameraHelper.openCam();
        }
    }

    public static void openCam() {
        if (isCameraAvailable()) {
            try {
                Webcam webcam = Webcam.getDefault();
                if (!webcam.getLock().isLocked() && !webcam.isOpen()) {
                    webcam.open();
                    SdpoLog.info("Camera opened successfully.");
                } else {
                    SdpoLog.info("Camera is already open or locked.");
                }
            } catch (WebcamException e) {
                SdpoLog.error("Failed to open camera: " + e.getMessage());
            }
        } else {
            SdpoLog.info("No camera available. Skipping camera open.");
        }
    }

    public static void closeCam() {
        if (isCameraAvailable() && Webcam.getDefault().isOpen()) {
            Webcam.getDefault().close();
        }
    }

    public static String makePhoto(String name) throws IOException {
        if (isCameraAvailable()) {
            BufferedImage image = Webcam.getDefault().getImage();
            return savePhoto(image, name);
        } else {
            SdpoLog.info("No camera available. Skipping photo capture.");
            return null;
        }
    }

    public static JSONObject makePhotoAndVideo() {
        return makePhotoAndVideo("");
    }

    public static JSONObject makePhotoAndVideo(String driver_id) {
        JSONObject json = new JSONObject();
        if (isCameraAvailable()) {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_k-m-s-S");
            String name = simpleDateFormat.format(date);

            if (!driver_id.isEmpty()) {
                name += '_' + driver_id;
            }

            json.put("photo", MultipartUtility.BACKEND_URL + "/get_file/photo/" + name + ".png");
            json.put("video", MultipartUtility.BACKEND_URL + "/get_file/video/" + name + ".mp4");
            MediaMakeTask.record(name);
        } else {
            SdpoLog.info("No camera available. Skipping photo and video capture.");
        }
        return json;
    }

    public static void makeVideo(String name) {
        if (isCameraAvailable()) {
            String path = FileBase.concatPath(FileBase.getMainFolderUrl(), "video", name + ".mp4");
            new File(path).getParentFile().mkdirs();
            recordScreen(path, 10);
            SdpoLog.info("Saving video");
            saveVideo(name);
        } else {
            SdpoLog.info("No camera available. Skipping video capture.");
        }
    }
    private static void recordScreen(String filename, int duration) {
        Webcam webcam = Webcam.getDefault();
        IMediaWriter writer = ToolFactory.makeWriter(filename);
        Dimension size = webcam.getViewSize();

        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

        long start = System.currentTimeMillis();

       int i = 0;
       while (System.currentTimeMillis() - start < duration * 1000L) {
            BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

            IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
            frame.setKeyFrame(i == 0);
            frame.setQuality(100);
            writer.encodeVideo(0, frame);
           try {
               Thread.sleep(20);
           } catch (InterruptedException e) {
               /* ignored */
           }
           i++;
        }

        writer.close();
        SdpoLog.info("Video recorded to the file: " + filename);
    }

    public static Map<Integer, Dimension> getSizes() {
        Map<Integer, Dimension> result = new HashMap<>();
        try {
            for (Dimension dim : Webcam.getDefault().getViewSizes()) {
                result.put((int) dim.getWidth(), dim);
            }
        } catch (WebcamException e) {
            SdpoLog.error("Error get webcam size: " + e);
        }

        return result;
    }

    public static Dimension getSize(Double width) {
        try {
            for (Dimension dim : Webcam.getDefault().getViewSizes()) {
                if (dim.getWidth() == width) {
                    return dim;
                }
            }

            if (Webcam.getDefault().getViewSizes().length > 0) {
                return Webcam.getDefault().getViewSizes()[0];
            }
        } catch (WebcamException e) {
            SdpoLog.error("Error get webcam size: " + e);
        }

        return null;
    }

    public static String getDefaultSize() {
        double result = 0;

        try {
            Dimension[] viewSizes = Webcam.getWebcams().isEmpty() ? new Dimension[0] : Webcam.getDefault().getViewSizes();
            for (Dimension dim : viewSizes) {
                if (result < dim.getWidth()) {
                    result = dim.getWidth();
                }
            }
        } catch (WebcamException e) {
            SdpoLog.warning("Error while getting webcam size: " + e.getMessage());
        }

        if (result == 0) {
            return null;
        }

        return String.valueOf((int) result);
    }



    public static byte[] readVideoByte() throws IOException {
        if (lastResultVideo == null) {
            return null;
        }

        FileInputStream inputStream = new FileInputStream(lastResultVideo);
        ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bufferedOutputStream.write(data, 0, nRead);
        }
        bufferedOutputStream.flush();
        return bufferedOutputStream.toByteArray();
    }

    public static String savePhoto(BufferedImage image, String name) throws IOException {
        String path = FileBase.concatPath(FileBase.getMainFolderUrl(), "image", name + ".png");

        File photo = new File(path);

        photo.getParentFile().mkdirs();
        ImageIO.write(image, "png", photo);

        if (!photo.exists() || photo.isDirectory()) {
            return null;
        }

        if (Sdpo.isConnection()) {
            sendPhoto(photo);
        }

        getLastResultPhoto = photo;

        return MultipartUtility.BACKEND_URL + "/get_file/photo/" + name + ".png";
    }

    public static void sendPhoto(File file) {
        int dotIndex = file.getName().lastIndexOf('.');
        String name = (dotIndex == -1) ? file.getName() : file.getName().substring(0, dotIndex);

        new Thread(() -> {
            try {
                MultipartUtility multipartUtility = new MultipartUtility("/send_file/", "UTF-8");
                multipartUtility.addFormField("type_content", "photo");
                multipartUtility.addFormField("name",  name);
                multipartUtility.addFilePart("filename", file);
                multipartUtility.finish();
            } catch (Exception e) {
                SdpoLog.error(e);
            }
        }).start();
    }

    public static void saveVideo(String name) {
        String path = FileBase.concatPath(FileBase.getMainFolderUrl(), "video", name + ".mp4");
        File video = new File(path);

        if (!video.exists() || video.isDirectory()) {
            return;
        }

        if (Sdpo.isConnection()) {
            sendVideo(video);
        }

        lastResultVideo = video;
    }

    public static void sendVideo(File file) {
        int dotIndex = file.getName().lastIndexOf('.');
        String name = (dotIndex == -1) ? file.getName() : file.getName().substring(0, dotIndex);

        new Thread(() -> {
            try {
                MultipartUtility multipartUtility = new MultipartUtility("/send_file/", "UTF-8");
                multipartUtility.addFormField("type_content", "video");
                multipartUtility.addFormField("name", name);
                multipartUtility.addFilePart("filename", file);
                multipartUtility.finish();
            } catch (Exception e) {
                SdpoLog.error(e);
            }
        }).start();
    }
}
