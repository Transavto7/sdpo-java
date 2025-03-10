package ru.nozdratenko.sdpo.helper.CameraHelpers;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.Core.Network.MultipartUtility;
import ru.nozdratenko.sdpo.task.MediaMakeTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("production")
public class WindowsCameraHelper implements CameraHelper {
    public File lastResultVideo = null;
    private FrameGrabber workWebcam = null;
    private transient boolean isCameraWorked = false;

    public boolean isCameraAvailable() {
        try {
            return !Webcam.getWebcams().isEmpty();
        } catch (WebcamException e) {
            SdpoLog.warning("No webcam has been detected!" + e);
            return false;
        }
    }

    private FrameGrabber findWebcam(){
        if(workWebcam == null){
            return this.findWebcam(true);
        }
        return workWebcam;
    }

    private FrameGrabber findWebcam(boolean isDefaultWebcam){

        if(workWebcam == null && isCameraAvailable()) {

            List<Webcam> webcams = Webcam.getWebcams();
            SdpoLog.info( String.format("Find %s camers: [ %s ]", webcams.size(), webcams.stream().map(entry -> entry.getName())
                    .collect(Collectors.joining("], ["))));
            if(!isDefaultWebcam && webcams.size() > 1) {
                SdpoLog.info("Entered SECOND camera.");
                workWebcam = new OpenCVFrameGrabber(1);
            } else {
                SdpoLog.info("Entered DEFAULT camera.");
                workWebcam = new OpenCVFrameGrabber(0);
            }

            if(webcams == null){
                SdpoLog.info("Camera not selected!");
            }

        }

        return workWebcam;
    }

    public void initDimension() {
        try {
            Webcam webcam = Webcam.getDefault();
            double with = Double.parseDouble(Sdpo.settings.systemConfig.getString("camera_dimension"));
            Dimension dim = getSize(with);
            webcam.setViewSize(dim);
        } catch (IllegalArgumentException e) {
            SdpoLog.warning(e);
        } finally {
            SdpoLog.info("Camera dimensions initialized");
            openCam();
        }
    }

    public void openCam() {
        if(isCameraWorked){
            SdpoLog.info("Camera is open.");
        }else if (isCameraAvailable()) {
            try {
                workWebcam = findWebcam();
                SdpoLog.info("Opening camera");
                workWebcam.start();
                isCameraWorked = true;
                SdpoLog.info("Camera opened successfully.");

            } catch (FrameGrabber.Exception e) {
                SdpoLog.error("Failed to open camera: " + e);
            }
        } else {
            SdpoLog.info("No camera available. Skipping camera open.");
        }
    }

    public void closeCam() {
        if (isCameraAvailable()) {
            try {
                findWebcam().close();
                workWebcam = null;
                isCameraWorked = false;
            } catch (FrameGrabber.Exception e) {
                SdpoLog.error("Failed to close camera: " + e);
            }
            SdpoLog.info("Camera closed.");
        }
    }

    public Dimension getSize(Double width) {
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

    public Map<Integer, Dimension> getSizes() {
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

    public String getDefaultSize() {
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

    public byte[] makePhotoBytes() throws IOException{
        if (isCameraAvailable()) {
            SdpoLog.info("Make a foto, width name camera: " + Webcam.getDefault().getName());

            org.bytedeco.javacv.Frame frame = null;
            workWebcam = findWebcam();

            frame = workWebcam.grab();
            SdpoLog.info("Image capture " + ( frame == null ? "is empty" : "successful"));
            if (frame != null) {
                BufferedImage im = new Java2DFrameConverter().convert(frame);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes = null;
                try{
                    ImageIO.write(im,"jpg",baos);
                    baos.flush();
                    bytes = baos.toByteArray();
                }finally{
                    baos.close();
                }
                SdpoLog.info("Frame converted to foto. Image size: " + bytes.length);
                frame.close();
                return bytes;
            } else {
                SdpoLog.info("Failed to capture image.");
            }
        } else {
            SdpoLog.info("No camera available. Skipping photo capture.");
        }
        return null;
    }

    public String makePhoto(String name) throws IOException {
        if (isCameraAvailable()) {
            SdpoLog.info("Make a foto, width name camera: " + Webcam.getDefault().getName());

            org.bytedeco.javacv.Frame frame = null;
            workWebcam = findWebcam();
            try {
                workWebcam.grab();
                frame = workWebcam.grabFrame();
            } catch (FrameGrabber.Exception e) {
                SdpoLog.error(e);
            }

            if (frame != null) {
                String photo = savePhoto(frame, name);
                SdpoLog.info("Maked a foto is done! Path: " + photo);
                frame.close();
                return photo;
            } else {
                SdpoLog.info("Failed to capture image.");
                closeCam();
                openCam();
            }
        } else {
            SdpoLog.info("No camera available. Skipping photo capture.");

        }
        return null;
    }

    public void stopMediaIntoTask(){
        MediaMakeTask.mediaLastKill();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            /* ignored */
        }
    }

    public JSONObject makePhotoAndVideo(){
        return makePhotoAndVideo("");
    }

    public JSONObject makePhotoAndVideo(String driver_id) {
        JSONObject json = new JSONObject();
        if (isCameraAvailable()) {
            MediaMakeTask.skip = false;
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

    public void makeVideo(String name) {
        if (getPathRecordVideo(name) != null) {
            SdpoLog.info("Saving video");
            saveVideo(name);
        } else {
            SdpoLog.info("No camera available. Skipping video capture.");
        }
    }

    private String getPathRecordVideo(String name){
        if (!isCameraAvailable()) {
            SdpoLog.info("No camera available.");
            return null;
        }
        String path = FileBase.concatPath(FileBase.getMainFolderUrl(), "video", name + ".mp4");
        SdpoLog.info("Make video with path: " + path);
        new File(path).getParentFile().mkdirs();
        recordScreen(path, 120);
        return path;
    }

    private void recordScreen(String filename, int duration) {
        try {
            workWebcam = findWebcam();

            FrameRecorder recorder = new FFmpegFrameRecorder(filename,
                    workWebcam.getImageWidth(), workWebcam.getImageHeight());
            recorder.setFrameRate(workWebcam.getFrameRate());
            recorder.setVideoCodec( avcodec.AV_CODEC_ID_H264 );
            recorder.setPixelFormat( avutil.AV_PIX_FMT_YUV420P );
            recorder.setFormat("mp4");
            recorder.start();

            long start = System.currentTimeMillis();

            org.bytedeco.javacv.Frame layer = null;
            while (!MediaMakeTask.skip && ((layer = workWebcam.grabFrame()) != null && System.currentTimeMillis() - start < duration * 1000L)) {
                recorder.record(layer);
            }
            SdpoLog.info("Stop capturing video");
            recorder.stop();
            if(Objects.nonNull(layer)) layer.close();
            recorder.release();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            SdpoLog.error("Error capturing video:");
            SdpoLog.error(e);
            closeCam();
            openCam();
        }
        MediaMakeTask.skip = false;
    }

    public byte[] readVideoByte() throws IOException {
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

    public String savePhoto(Frame image, String name) {
        String mainFolderUrl = FileBase.getMainFolderUrl();
        boolean isCyrillic = mainFolderUrl.matches(".*[\\u0400-\\u04FF].*");
        String path = FileBase.concatPath(mainFolderUrl, "image", name + ".png");
        File photo = new File(path);

        photo.getParentFile().mkdirs();

        try {
            photo.createNewFile();
        } catch (IOException e) {
            SdpoLog.error(e.getMessage());
        }
        if (!photo.exists() || photo.isDirectory()) {
            SdpoLog.error(!photo.exists() ? "Photo is not exists" : "Photo is directory!");
            return null;
        }

        if (image != null && image.image != null) {
            if (isCyrillic) {
                SdpoLog.info("Save photo with Cyrillic path: " + path);
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage bufferedImage = converter.convert(image);

                    if (bufferedImage != null) {
                        try {
                            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
                            if (!writers.hasNext()) {
                                SdpoLog.error("No PNG writer found");
                                return null;
                            }
                            ImageWriter writer = writers.next();
                            ImageOutputStream ios = ImageIO.createImageOutputStream(photo);
                            writer.setOutput(ios);

                            ImageWriteParam param = writer.getDefaultWriteParam();
                            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                            param.setCompressionQuality(0.1f);

                            writer.write(null, new javax.imageio.IIOImage(bufferedImage, null, null), param);
                            ios.close();
                            writer.dispose();
                        } catch (IOException e) {
                            SdpoLog.error("Failed to save photo: " + e.getMessage());
                            return null;
                        }

                    } else {
                        SdpoLog.error("BufferedImage conversion failed");
                        return null;
                    }

                }
            } else {
                SdpoLog.info("Save photo with Latin path: " + path);
                try (OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage()) {
                    IplImage mat = converter.convert(image);
                    opencv_imgcodecs.cvSaveImage(path, mat);
                } catch (Exception e) {
                    SdpoLog.error("Failed to save photo: " + e.getMessage());
                    return null;
                }
            }
        } else {
            SdpoLog.error("Frame or Image is empty");
        }

        if (Sdpo.isConnection()) {
            sendPhoto(photo);
        }

        return MultipartUtility.BACKEND_URL + "/get_file/photo/" + name + ".png";
    }

    public void sendPhoto(File file) {
        int dotIndex = file.getName().lastIndexOf('.');
        String name = (dotIndex == -1) ? file.getName() : file.getName().substring(0, dotIndex);
        SdpoLog.info("Send photo to server with name: " + name);
        new Thread(() -> {
            try {
                SdpoLog.info("Send photo in new thread with name: " + name);
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

    public void saveVideo(String name) {
        String path = FileBase.concatPath(FileBase.getMainFolderUrl(), "video", name + ".mp4");

        SdpoLog.info("Save video with path: " + path);
        File video = new File(path);

        if (!video.exists() || video.isDirectory()) {
            SdpoLog.error("File is not exists or directory");
            return;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (Sdpo.isConnection()) {
            sendVideo(video);
        }else{
            SdpoLog.error("Video driver is not connection!");
        }

        lastResultVideo = video;


    }

    public void sendVideo(File file) {
        int dotIndex = file.getName().lastIndexOf('.');
        String name = (dotIndex == -1) ? file.getName() : file.getName().substring(0, dotIndex);
        SdpoLog.info("Send video to server with name: " + name);
        new Thread(() -> {
            try {
                SdpoLog.info("Send video in new thread with name: " + name);
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
