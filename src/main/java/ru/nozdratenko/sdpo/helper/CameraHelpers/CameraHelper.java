package ru.nozdratenko.sdpo.helper.CameraHelpers;

import org.bytedeco.javacv.Frame;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
