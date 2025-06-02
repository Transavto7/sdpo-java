package ru.nozdratenko.sdpo.util.port.PortService;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.port.Cfgmgr32;
import ru.nozdratenko.sdpo.util.port.SetupApi;
import ru.nozdratenko.sdpo.util.thread.ThreadUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Profile("production")
public class WindowsPortService implements PortService {
    private static final int DIGCF_PRESENT = 0x00000002;
    private static final int DIGCF_DEVICEINTERFACE = 0x00000010;
    private static final int MAX_ATTEMPTS = 5;

    int checkDeviceStateCounter = 0;

    @Override
    public boolean reinitializePort(String deviceInstanceId) {
        try {
            while (isDeviceInProblemState(deviceInstanceId)) {
                if (checkDeviceStateCounter > MAX_ATTEMPTS) {
                    SdpoLog.error("Reached maximum attempts to reinit port: " + deviceInstanceId);
                    return false;
                }

                SdpoLog.info("Alkometer port has a problem before restarting: " + deviceInstanceId);

                if (!consoleCommandExecution("Disable", deviceInstanceId)) {
                    SdpoLog.info("Disable command failed for device: " + deviceInstanceId);
                    return false;
                }

                ThreadUtil.suspendCurrentAction(1000L);

                if (!consoleCommandExecution("Enable", deviceInstanceId)) {
                    SdpoLog.info("Enable command failed for device: " + deviceInstanceId);
                    return false;
                }

                ThreadUtil.suspendCurrentAction(3000L);

                if (!consoleCommandExecution("Restart", deviceInstanceId)) {
                    SdpoLog.info("Restart command failed for device: " + deviceInstanceId);
                    return false;
                }

                ThreadUtil.suspendCurrentAction(3000L);
                checkDeviceStateCounter = 0;
                return true;
            }
        } catch (Exception e) {
            SdpoLog.error(e);
        }
        SdpoLog.info("Failed to reinit port for device: " + deviceInstanceId);
        return false;
    }

    private boolean consoleCommandExecution(String command, String deviceInstanceId) throws IOException, InterruptedException {
        String[] pnpCommand = {"cmd.exe", "/c", String.format("PNPUTIL /%s-device \"%s\"", command.toLowerCase(), deviceInstanceId)};
        ProcessBuilder processBuilder = new ProcessBuilder(pnpCommand);
        Process pnpProcess = processBuilder.start();
        logProcessOutput(pnpProcess);
        int exitCode = pnpProcess.waitFor();
        if (exitCode != 0) {
            SdpoLog.info(String.format("%s command exit code: %s", command, exitCode));
        }
        return exitCode == 0;
    }

    private boolean isDeviceInProblemState(String deviceInstanceId) throws IOException, InterruptedException {
    String[] pnpCommandCheckProblem = {"cmd.exe", "/c", "PNPUTIL /enum-devices /problem"};
    ProcessBuilder processBuilder = new ProcessBuilder(pnpCommandCheckProblem);
    Process process = processBuilder.start();

    boolean problemDetected = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
//            SdpoLog.info("isDeviceInProblemState: " + line);
                if (line.contains(deviceInstanceId)) {
                    problemDetected = true;
                    break;
                }
            }
        }
        process.waitFor();
        if (checkDeviceStateCounter == 0 && !problemDetected){
            SdpoLog.warning("Check connection to Alcometer !!! InstanceId: " + deviceInstanceId);
        }
        checkDeviceStateCounter++;
        return problemDetected;
    }

    private void logProcessOutput(Process process) throws IOException {
        String line;
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            while ((line = errorReader.readLine()) != null) {
                SdpoLog.info("ERROR: " + line);
            }
        }
    }


    @Override
    public String getDeviceInstanceId(String guidString, String vendorId) {
        GUID guid = new GUID(guidString);
        HANDLE deviceInfoSet = ru.nozdratenko.sdpo.util.port.SetupApi.INSTANCE.SetupDiGetClassDevs(guid, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);

        if (deviceInfoSet == WinBase.INVALID_HANDLE_VALUE) {
            SdpoLog.info("Failed to get device list");
            return null;
        }

        try {
            SpDevinfoData deviceInfoData = new SpDevinfoData();
            deviceInfoData.cbSize = deviceInfoData.size();

            int index = 0;
            while (ru.nozdratenko.sdpo.util.port.SetupApi.INSTANCE.SetupDiEnumDeviceInfo
                    (deviceInfoSet, index, deviceInfoData)) {
                index++;

                String currentDeviceInstanceId = getDeviceInstanceId(deviceInfoData);
                if (currentDeviceInstanceId != null && currentDeviceInstanceId.contains(vendorId)) {
                    SdpoLog.info("Port is available. Device Instance Id: " + currentDeviceInstanceId);
                    return currentDeviceInstanceId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SetupApi.INSTANCE.SetupDiDestroyDeviceInfoList(deviceInfoSet);
        }

        SdpoLog.info("getDeviceInstanceId: Device with vendorId " + vendorId + " not found");
        return null;
    }

    private String getDeviceInstanceId(SpDevinfoData deviceInfoData) {
        char[] buffer = new char[1024];
        int result = ru.nozdratenko.sdpo.util.port.Cfgmgr32.INSTANCE.CM_Get_Device_ID(deviceInfoData.DevInst, buffer, buffer.length, 0);
        if (result == Cfgmgr32.CR_SUCCESS) {
            String devId = Native.toString(buffer);
            return devId;
        }
        return null;
    }

}