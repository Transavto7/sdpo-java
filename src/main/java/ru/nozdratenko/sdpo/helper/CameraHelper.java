package ru.nozdratenko.sdpo.helper;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.network.MultipartUtility;
import ru.nozdratenko.sdpo.task.MediaMakeTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CameraHelper {
    public static File lastResultVideo = null;
    public static File getLastResultPhoto = null;
    private static FrameGrabber workWebcam = null;

    public static boolean isCameraAvailable() {
        try {

            return !Webcam.getWebcams().isEmpty();
        } catch (WebcamException e) {
            SdpoLog.warning("No webcam has been detected!" + e);
            return false;
        }
    }

    private static FrameGrabber findWebcam(){
        if(workWebcam == null){
            findWebcam(true);
        }
        return workWebcam;
    }

    private static FrameGrabber findWebcam(boolean isDefaultWebcam){

        if(workWebcam == null && isCameraAvailable()) {

            List<Webcam> webcams = Webcam.getWebcams();
            SdpoLog.info( String.format("Find %s camers: [ %s ]", (webcams.size() + 1), webcams.stream().map(entry -> entry.getName())
                    .collect(Collectors.joining(", "))));
            if(webcams.size() > 1 && !isDefaultWebcam){
                workWebcam = new OpenCVFrameGrabber(1);
            }else {
                SdpoLog.info("This is the default camera.");
                workWebcam = new OpenCVFrameGrabber(0);
            }

            if(webcams == null){
                SdpoLog.info("Camera not selected!");
            }

            SdpoLog.info( String.format("Selected %s camera", workWebcam.getOptions().toString()));
        }

        return workWebcam;
    }

    public static void initDimension() {
        try {
            Webcam webcam = Webcam.getDefault();
            double with = Double.parseDouble(Sdpo.systemConfig.getString("camera_dimension"));
            Dimension dim = getSize(with);
            webcam.setViewSize(dim);
        } catch (IllegalArgumentException e) {
            SdpoLog.warning(e);
        } finally {
            SdpoLog.info("Camera dimensions initialized");
        }
    }

    public static void openCam() {
        if (isCameraAvailable()) {
            try {
                workWebcam = findWebcam();
                workWebcam.start();
                SdpoLog.info("Camera opened successfully.");

            } catch (FrameGrabber.Exception e) {
                SdpoLog.error("Failed to open camera: " + e);
            }
        } else {
            SdpoLog.info("No camera available. Skipping camera open.");
        }
    }

    public static void closeCam() {
        if (isCameraAvailable()) {
            try {
                findWebcam().close();
            } catch (FrameGrabber.Exception e) {
                SdpoLog.error("Failed to close camera: " + e);
            }
            SdpoLog.info("Camera closed.");
        }
    }

    public static Dimension getSize(Double width) {
        try {
            Webcam webcam = Webcam.getDefault();
            for (Dimension dim : webcam.getViewSizes()) {
                if (dim.getWidth() == width) {
                    return dim;
                }
            }

            if (webcam.getViewSizes().length > 0) {
                return webcam.getViewSizes()[0];
            }
        } catch (WebcamException e) {
            SdpoLog.error("Error get webcam size: " + e);
        }

        return null;
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
            SdpoLog.warning("Error while getting webcam size: " + e);
        }

        if (result == 0) {
            return null;
        }

        return String.valueOf((int) result);
    }

    public static String makePhoto(String name) throws IOException {
        if (isCameraAvailable()) {
            SdpoLog.info("Make a foto, width name camera: " + Webcam.getDefault().getName());

            org.bytedeco.javacv.Frame frame = null;
            workWebcam = findWebcam();
            openCam();
            frame = workWebcam.grab();
            SdpoLog.info("Make a foto, is not null: " + (frame != null));
            if (frame != null) {
                String photo = savePhoto(frame, name);
                SdpoLog.info("Make a foto, width name: " + photo);
                closeCam();
                frame.close();
                return photo;
            } else {
                SdpoLog.info("Failed to capture image.");
                return null;
            }
        } else {
            SdpoLog.info("No camera available. Skipping photo capture.");
            return null;
        }
    }
    public static JSONObject makePhotoAndVideo(){
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
            SdpoLog.info("Make video with path: " + path);
            new File(path).getParentFile().mkdirs();
            recordScreen(path, 10);
            SdpoLog.info("Saving video");
            saveVideo(name);
        } else {
            SdpoLog.info("No camera available. Skipping video capture.");
        }
    }
    private static void recordScreen(String filename, int duration) {
        try {
            workWebcam = findWebcam();
            openCam();

            FrameRecorder recorder = new FFmpegFrameRecorder(filename,
                    workWebcam.getImageWidth(), workWebcam.getImageHeight());
            recorder.setFrameRate(workWebcam.getFrameRate());
            recorder.setVideoCodec( avcodec.AV_CODEC_ID_H264 );
            recorder.setPixelFormat( avutil.AV_PIX_FMT_YUV420P );
            recorder.setFormat("mp4");
            recorder.start();

            long start = System.currentTimeMillis();

            org.bytedeco.javacv.Frame layer;
            while ((layer = workWebcam.grabFrame()) != null && System.currentTimeMillis() - start < duration * 1000L) {
                recorder.record(layer);
            }

            recorder.stop();
            closeCam();
            recorder.release();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            SdpoLog.error("Error capturing video:");
            SdpoLog.error(e);
        }

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

    public static String savePhoto(Frame image, String name) throws IOException {
        String path = FileBase.concatPath(FileBase.getMainFolderUrl(), "image", name + ".png");
        SdpoLog.info("Save photo with path: " + path);
        File photo = new File(path);

        photo.getParentFile().mkdirs();

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage img = converter.convert(image);
        opencv_imgcodecs.cvSaveImage(path, img);

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
                SdpoLog.info("Send photo to server with name: " + name);
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

        SdpoLog.info( "Save video with path: " + path);
        File video = new File(path);

        if (!video.exists() || video.isDirectory()) {
            SdpoLog.error("File is not exists or directory");
            return;
        }

        if (Sdpo.isConnection()) {
            sendVideo(video);
        }else{
            SdpoLog.error("Video driver is not connection!");
        }

        lastResultVideo = video;
    }

    public static void sendVideo(File file) {
        int dotIndex = file.getName().lastIndexOf('.');
        String name = (dotIndex == -1) ? file.getName() : file.getName().substring(0, dotIndex);

        new Thread(() -> {
            try {
                SdpoLog.info("Send video to server with name: " + name);
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
