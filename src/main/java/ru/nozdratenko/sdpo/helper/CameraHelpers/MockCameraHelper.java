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

@Service
@Profile("develop")
public class MockCameraHelper implements CameraHelper {
    public boolean isCameraAvailable() {
        return false;
    }

    public void initDimension() {
        SdpoLog.info("MockCameraHelper::initDimension");
    }

    public void openCam() {
        SdpoLog.info("MockCameraHelper::openCam");
    }

    public void closeCam() {
        SdpoLog.info("MockCameraHelper::closeCam");
    }

    public Dimension getSize(Double width) {
        SdpoLog.info("MockCameraHelper::getSize");
        return null;
    }

    public Map<Integer, Dimension> getSizes() {
        SdpoLog.info("MockCameraHelper::getSize");
        return new HashMap<>();
    }

    public String getDefaultSize() {
        SdpoLog.info("MockCameraHelper::getDefaultSize");

        return "10";
    }

    public byte[] makePhotoBytes() {
        SdpoLog.info("MockCameraHelper::makePhotoBytes");

        return null;
    }

    public String makePhoto(String name) throws IOException {
        SdpoLog.info("MockCameraHelper::makePhoto");

        return null;
    }

    public void stopMediaIntoTask(){
        SdpoLog.info("MockCameraHelper::stopMediaIntoTask");
    }

    public JSONObject makePhotoAndVideo(){
        SdpoLog.info("MockCameraHelper::makePhotoAndVideo");
        return makePhotoAndVideo("");
    }

    public JSONObject makePhotoAndVideo(String driver_id) {
        SdpoLog.info("MockCameraHelper::makePhotoAndVideo");

        return new JSONObject();
    }

    public void makeVideo(String name) {
        SdpoLog.info("MockCameraHelper::makeVideo");
    }

    public byte[] readVideoByte() {
        SdpoLog.info("MockCameraHelper::readVideoByte");
        return null;
    }

    public String savePhoto(Frame image, String name) {
        SdpoLog.info("MockCameraHelper::savePhoto");

        return MultipartUtility.BACKEND_URL + "/get_file/photo/" + name + ".png";
    }

    public void sendPhoto(File file) {
        SdpoLog.info("MockCameraHelper::sendPhoto");
    }

    public void saveVideo(String name) {
        SdpoLog.info("MockCameraHelper::saveVideo");
    }

    public void sendVideo(File file) {
        SdpoLog.info("MockCameraHelper::sendVideo");
    }

}
