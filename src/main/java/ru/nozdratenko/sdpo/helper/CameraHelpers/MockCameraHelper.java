package ru.nozdratenko.sdpo.helper.CameraHelpers;

import org.bytedeco.javacv.Frame;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Core.Network.MultipartUtility;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    public byte[] makePhotoBytes() throws IOException {
        SdpoLog.info("MockCameraHelper::makePhotoBytes");
        ClassPathResource resource = new ClassPathResource("printer.jpg");
        Path filePath = Path.of(resource.getURI());

        return Files.readAllBytes(filePath);
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

    public byte[] readVideoByte() throws IOException {
        SdpoLog.info("MockCameraHelper::readVideoByte");
        ClassPathResource resource = new ClassPathResource("printer.jpg");
        Path filePath = Path.of(resource.getURI());

        return Files.readAllBytes(filePath);
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
