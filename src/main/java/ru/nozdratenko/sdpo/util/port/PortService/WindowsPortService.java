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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Profile("production")
public class WindowsPortService implements PortService {
    public static final int DIGCF_PRESENT = 0x00000002;
    public static final int DIGCF_DEVICEINTERFACE = 0x00000010;
    private static final int TOKEN_QUERY = 0x0008;
    private static final int TokenElevation = 20;

    public boolean reinitializePort(String deviceInstanceId) {
        try {
            int exitCodeDisable = 0;
            int exitCodeEnable = 0;
            int exitCodeRestart = 0;
            while (isDeviceInProblemState(deviceInstanceId)) {
                SdpoLog.info("Alkometer port still has a problem before restarting: " + deviceInstanceId);
                // Команда для отключения устройства
                String[] pnpCommandDisable = {"cmd.exe", "/c", String.format("PNPUTIL /disable-device \"%s\"", deviceInstanceId)};
                ProcessBuilder pnpPbDisable = new ProcessBuilder(pnpCommandDisable);
                Process pnpProcessDisable = pnpPbDisable.start();
                logProcessOutput(pnpProcessDisable);
                exitCodeDisable = pnpProcessDisable.waitFor();
                if (exitCodeDisable != 0) {
                    SdpoLog.info("Disable command exit code: " + exitCodeDisable);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String[] pnpCommandEnable = {"cmd.exe", "/c", String.format("PNPUTIL /enable-device \"%s\"", deviceInstanceId)};
                ProcessBuilder pnpPbEnable = new ProcessBuilder(pnpCommandEnable);
                Process pnpProcessEnable = pnpPbEnable.start();
                logProcessOutput(pnpProcessEnable);
                exitCodeEnable = pnpProcessEnable.waitFor();
                if (exitCodeEnable != 0) {
                    SdpoLog.info("Enable command exit code: " + exitCodeEnable);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String[] pnpCommandRestart = {"cmd.exe", "/c", String.format("PNPUTIL /restart-device \"%s\"", deviceInstanceId)};
                ProcessBuilder pnpPbRestart = new ProcessBuilder(pnpCommandRestart);
                Process pnpProcessRestart = pnpPbRestart.start();
                logProcessOutput(pnpProcessRestart);
                exitCodeRestart = pnpProcessRestart.waitFor();
                if (exitCodeRestart != 0) {
                    SdpoLog.info("Restart command exit code: " + exitCodeRestart);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return exitCodeDisable == 0 && exitCodeEnable == 0 && exitCodeRestart == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDeviceInProblemState(String deviceInstanceId) throws IOException, InterruptedException {
        String[] pnpCommandCheckProblem = {"cmd.exe", "/c", "PNPUTIL /enum-devices /problem"};
        ProcessBuilder pnpPbCheckProblem = new ProcessBuilder(pnpCommandCheckProblem);
        Process pnpProcessCheckProblem = pnpPbCheckProblem.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(pnpProcessCheckProblem.getInputStream()));
        String line;
        boolean problemDetected = false;
        while ((line = reader.readLine()) != null) {
//            SdpoLog.info("isDeviceInProblemState: " + line);
            if (line.contains(deviceInstanceId)) {
                problemDetected = true;
                break;
            }
        }
        pnpProcessCheckProblem.waitFor();
        return problemDetected;
    }

    private void logProcessOutput(Process process) throws IOException {
        String line;
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            SdpoLog.info("ERROR: " + line);
        }
    }


    public String getDeviceInstanceId(String guidString, String vendorId) {
        SdpoLog.info("getDeviceInstanceId for vendorId: " + vendorId);
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

    public boolean isAdmin() {
        HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        WinNT.HANDLEByReference tokenHandle = new WinNT.HANDLEByReference();

        if (Advapi32.INSTANCE.OpenProcessToken(processHandle, TOKEN_QUERY, tokenHandle)) {
            WinNT.TOKEN_ELEVATION elevation = new WinNT.TOKEN_ELEVATION();
            IntByReference tokenInformationLength = new IntByReference(elevation.size());

            boolean result = Advapi32.INSTANCE.GetTokenInformation(tokenHandle.getValue(), TokenElevation, elevation, elevation.size(), tokenInformationLength);
            return result && elevation.TokenIsElevated != 0;
        }

        return false;
    }

}