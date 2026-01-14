package ru.nozdratenko.sdpo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.Settings.CoreConfigurations.FileConfiguration;
import ru.nozdratenko.sdpo.Settings.Factories.SettingsFactory;
import ru.nozdratenko.sdpo.Settings.SettingsContainer;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.helper.AdminHelpers.AdminHelper;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.helper.BrowserHelpers.BrowserHelper;
import ru.nozdratenko.sdpo.helper.CameraHelpers.CameraHelper;
import ru.nozdratenko.sdpo.helper.DeviceHelper;
import ru.nozdratenko.sdpo.helper.EnvironmentMonitorHelpers.EnvironmentMonitorHelper;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;
import ru.nozdratenko.sdpo.storage.MedicStorage;
import ru.nozdratenko.sdpo.storage.StampStorage;
import ru.nozdratenko.sdpo.task.MediaMakeTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class Sdpo {
    private final AlcometerHelper alcometerHelper;
    private final ThermometerHelper thermometerHelper;
    private final List<DeviceHelper> helpers;
    private final BrowserHelper browserHelper;
    private final CameraHelper cameraHelper;
    private final AdminHelper adminHelper;
    private final EnvironmentMonitorHelper environmentMonitorHelper;
    private Environment env;

    public static SettingsContainer settings;
    public static FileConfiguration connectionConfig;

    public static StampStorage serviceDataStorage;
    private static MedicStorage medicStorage;

    public static final MediaMakeTask mediaMakeTask = new MediaMakeTask();

    @Getter
    private static boolean connection = true;
    @Getter
    private static boolean initialized = true;

    private List<String> toBlockDevices;

    public boolean init() {
        SdpoLog.info("Run project");

        this.initSettings();

        alcometerHelper.init();
        environmentMonitorHelper.init();

        if (!adminHelper.isAdmin()) {
            SdpoLog.warning("The program has been started without Admin role.");
            System.out.println("Программа была запущена без прав Администратора.");
            initialized = false;
            return false;
        }

        if (!Arrays.asList(env.getActiveProfiles()).contains("develop")) {
            checkServerConnection();

            if (!checkDeviceConnections()) {
                String disconnected = String.join(",", toBlockDevices);
                SdpoLog.warning(String.format("Please contact support, %s should be blocked.", disconnected));
                System.out.printf("Обратитесь в ТП, при этом %s в блокировку.", disconnected);
                initialized = false;
                return false;
            }
        }



        alcometerHelper.setComPort();
        thermometerHelper.setComPort();
        cameraHelper.initDimension();

        runTasks();

        return initialized;
    }

    public Map<String, Boolean> getDeviceStatuses() {
        return helpers.stream()
                .collect(Collectors.toMap(DeviceHelper::name, DeviceHelper::isDeviceConnected));
    }

    private boolean checkDeviceConnections() {
        Map<String, Boolean> deviceStatuses = getDeviceStatuses();
        boolean allConnected = true;

        for (Map.Entry<String, Boolean> entry : deviceStatuses.entrySet()) {
            if (!entry.getValue()) {
                SdpoLog.info(String.format("%s is disconnected", entry.getKey()));
                toBlockDevices.add(entry.getKey());
                allConnected = false;
            }
        }

        if (allConnected) {
            SdpoLog.info("All devices are connected");
        }

        return allConnected;
    }

    public void initSettings() {
        connectionConfig = SettingsFactory.makeConnectionConfig();
        settings = SettingsContainer.init();
    }

    private void checkServerConnection() {
        try {
            String address = Sdpo.connectionConfig.getString("url");

            if (!address.endsWith("/")) {
                address += "/";
            }

            Request request = new Request(new URL(address + "sdpo/check"));
            String response = request.sendGet();
            boolean connected = response.equals("true");
            Sdpo.setConnection(connected);
            SdpoLog.info(String.format("Root Server is %s", connected ? "connected" : "disconnected"));

        } catch (UnknownHostException ignored) {
        } catch (Exception | ApiException e) {
            SdpoLog.error(e);
        }
    }

    public void runTasks() {
        mediaMakeTask.start();
//        runScannerTask();
    }

    public void loadData() {
        medicStorage = new MedicStorage();
        medicStorage.saveToLocalStorage();

        serviceDataStorage = new StampStorage();
        serviceDataStorage.saveToLocalStorage();

        new Thread(() -> {
            try {
                medicStorage.getAllFromApi();
                medicStorage.saveToLocalStorage();

                serviceDataStorage.getAllFromApi();
                serviceDataStorage.saveToLocalStorage();
            } catch (IOException e) {
                SdpoLog.error(e);
            }
        }).start();
    }

    public void openBrowser() {
        new Thread(() -> {
            try {
                Thread.sleep(40000);
            } catch (InterruptedException e) {
                SdpoLog.error(e);
            }
            this.browserHelper.openUrl("http://localhost:8080");
        }).start();
    }

    public static void setConnection(boolean connection) {
        Sdpo.connection = connection;
    }
}