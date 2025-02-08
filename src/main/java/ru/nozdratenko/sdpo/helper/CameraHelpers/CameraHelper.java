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
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.network.MultipartUtility;
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
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public interface CameraHelper {
    boolean isCameraAvailable();
    void initDimension();
    void openCam();
    void closeCam();
    Dimension getSize(Double width);
    Map<Integer, Dimension> getSizes();
    String getDefaultSize();
    byte[] makePhotoBytes() throws IOException;
    String makePhoto(String name) throws IOException;
    void stopMediaIntoTask();
    JSONObject makePhotoAndVideo();
    JSONObject makePhotoAndVideo(String driver_id);
    void makeVideo(String name);
    byte[] readVideoByte() throws IOException;
    String savePhoto(Frame image, String name);
    void sendPhoto(File file);
    void saveVideo(String name);
    void sendVideo(File file);
}
