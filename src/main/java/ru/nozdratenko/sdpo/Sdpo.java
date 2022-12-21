package ru.nozdratenko.sdpo;

import com.github.sarxos.webcam.Webcam;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.file.FileConfiguration;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.helper.BrowserHelper;
import ru.nozdratenko.sdpo.helper.CameraHelper;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;
import ru.nozdratenko.sdpo.task.AlcometerResultTask;
import ru.nozdratenko.sdpo.task.BluetoothUpdateTask;
import ru.nozdratenko.sdpo.task.ThermometerResultTask;
import ru.nozdratenko.sdpo.task.TonometerResultTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Sdpo {
    public static FileConfiguration mainConfig;
    public static FileConfiguration systemConfig;

    public static final BluetoothUpdateTask bluetoothUpdateTask = new BluetoothUpdateTask();
    public static final TonometerResultTask tonometerResultTask = new TonometerResultTask();
    public static final ThermometerResultTask thermometerResultTask = new ThermometerResultTask();
    public static final AlcometerResultTask alcometerResultTask = new AlcometerResultTask();

    public static void init() {
        SdpoLog.info("Run project");
        initComPorts();
        initMainConfig();
        initSystemConfig();
        runTasks();
        BrowserHelper.openUrl("http://localhost:8080");
    }

    public static void initComPorts() {
        new Thread(() -> {
            ThermometerHelper.PORT = getComPort("VID_10C4");
            thermometerResultTask.start();
            AlcometerHelper.PORT = getComPort("VID_0483");
            alcometerResultTask.start();
            SdpoLog.info("Thermometer set port: " + ThermometerHelper.PORT);
            SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
            AlcometerHelper.reset();
        }).start();
    }

    public static void runTasks() {
        bluetoothUpdateTask.start();
        tonometerResultTask.start();
    }

    public static String getComPort(String vid) {
        String command = "powershell.exe  Get-PnpDevice -PresentOnly | Where-Object { $_.InstanceId -match '^USB\\\\" + vid + "' }";
        String result = null;

        try {
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();

            String line;
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                if (line.contains("(COM")) {
                    result = line.split("\\(COM")[1].substring(0, 1);
                    result = "COM" + result;
                }
            }
            stdout.close();

        } catch (IOException e) {
            SdpoLog.error("Not found com port " + vid + ": " + e);
        }

        return result;
    }

    private static void initMainConfig() {
        FileConfiguration fileConfiguration = new FileConfiguration("configs/main.json");
        fileConfiguration.setDefault("password", "7344946")
                .setDefault("url", "https://test.ta-7.ru/api")
                .setDefault("token", "$2y$10$2We7xBpaq1AxOhct.eTO4eq2G/winHxyNbfn.WsD7WbZw6rlMcLrS")
                .setDefault("tonometer_mac", null)
                .saveFile();
        mainConfig = fileConfiguration;
    }
    private static void initSystemConfig() {
        FileConfiguration fileConfiguration = new FileConfiguration("configs/system.json");
        fileConfiguration.setDefault("driver_info", false)
                .setDefault("type_ride", true)
                .setDefault("question_sleep", true)
                .setDefault("question_helth", true)
                .setDefault("alcometer_fast", false)
                .setDefault("alcometer_skip", true)
                .setDefault("alcometer_visible", true)
                .setDefault("tonometer_skip", true)
                .setDefault("tonometer_visible", true)
                .setDefault("camera_video", CameraHelper.getDefaultSize())
                .setDefault("camera_photo", CameraHelper.getDefaultSize())
                .setDefault("printer_write", true)
                .setDefault("thermometer_skip", true)
                .setDefault("thermometer_visible", true)
                .saveFile();
        systemConfig = fileConfiguration;
    }
}