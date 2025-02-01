package ru.nozdratenko.sdpo.util.port;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class PortService {

    public static class GUID extends Structure {
        public int Data1;
        public short Data2;
        public short Data3;
        public byte[] Data4 = new byte[8];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Data1", "Data2", "Data3", "Data4");
        }

        public GUID() {
        }

        public GUID(String guidStr) {
            UUID uuid = UUID.fromString(guidStr);
            long msb = uuid.getMostSignificantBits();
            long lsb = uuid.getLeastSignificantBits();
            Data1 = (int) (msb >> 32);
            Data2 = (short) (msb >> 16);
            Data3 = (short) msb;
            for (int i = 0; i < 8; i++) {
                Data4[i] = (byte) (lsb >> (8 * (7 - i)));
            }
        }
    }

    public static class SP_DEVINFO_DATA extends Structure {
        public int cbSize = size();
        public GUID ClassGuid = new GUID();
        public int DevInst;
        public Pointer Reserved;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("cbSize", "ClassGuid", "DevInst", "Reserved");
        }
    }

    public static final int DIGCF_PRESENT = 0x00000002;
    public static final int DIGCF_DEVICEINTERFACE = 0x00000010;

    public boolean reinitializePort(String deviceInstanceId, boolean checkDeviceInProblemState) {

        try {
            if (!isDeviceConnected(deviceInstanceId)){
                return false;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (checkDeviceInProblemState) {
            boolean reinited = false;
            int attemptCount = 0;
            try {
                while (isDeviceInProblemState(deviceInstanceId) && attemptCount < 5){
                    if (restartPort(deviceInstanceId)) {
                        reinited = true;
                    }
                    attemptCount++;
                }

            } catch (InterruptedException | IOException e) {
                SdpoLog.info("Failed to reinitializePort with checkDeviceInProblemState: " + e);
            }

            if (attemptCount > 0) {
                SdpoLog.info("Alkometer port has a problem before restarting: " + deviceInstanceId);
            } else {
                SdpoLog.info("Alkometer port has no any problems before restarting!");
            }

            return reinited;
        } else {
            return restartPort(deviceInstanceId);
        }
    }

    private boolean restartPort (String deviceInstanceId){
        try {
            int exitCodeDisable = executeConsoleCommand("Disable", deviceInstanceId);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int exitCodeEnable = executeConsoleCommand("Enable", deviceInstanceId);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int exitCodeRestart = executeConsoleCommand("Restart", deviceInstanceId);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return exitCodeDisable == 0 && exitCodeEnable == 0 && exitCodeRestart == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private int executeConsoleCommand(String phase, String deviceInstanceId) throws IOException, InterruptedException {
        String[] pnpCommand = {"cmd.exe", "/c", String.format("PNPUTIL /%s-device \"%s\"", phase.toLowerCase(), deviceInstanceId)};
        ProcessBuilder pnpProcessBuilder = new ProcessBuilder(pnpCommand);
        Process pnpProcess = pnpProcessBuilder.start();
        logProcessOutput(pnpProcess);
        int exitCode = pnpProcess.waitFor();
        if (exitCode != 0) {
            SdpoLog.info(String.format("%s command exit code: %s", phase, exitCode ));
        }
        return exitCode;
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

    private boolean isDeviceConnected (String deviceInstanceId) throws IOException, InterruptedException {
        String[] pnpCommandCheck = {"cmd.exe", "/c", String.format("PNPUTIL /enum-devices /instanceid \"%s\"", deviceInstanceId)};
        ProcessBuilder pnpPbCheck = new ProcessBuilder(pnpCommandCheck);
        Process pnpProcessCheck = pnpPbCheck.start();
        logProcessOutput(pnpProcessCheck);
        int exitCodeCheck = pnpProcessCheck.waitFor();
        if (exitCodeCheck != 0) {
            SdpoLog.info("Device not found or inaccessible: " + deviceInstanceId);
            return false;
        }
        SdpoLog.info("Device is detected: " + deviceInstanceId);
        return true;
    }

    private void logProcessOutput(Process process) throws IOException {
        String line;
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            SdpoLog.info("ERROR: " + line);
        }
    }


    String getDeviceInstanceId(String guidString, String vendorId) {
        List<String> ids = new ArrayList<>();
        SdpoLog.info("getDeviceInstanceId for vendorId: " + vendorId);
        GUID guid = new GUID(guidString);
        HANDLE deviceInfoSet = ru.nozdratenko.sdpo.util.port.SetupApi.INSTANCE.SetupDiGetClassDevs(guid, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);

        if (deviceInfoSet == WinBase.INVALID_HANDLE_VALUE) {
            SdpoLog.info("Failed to get device list");
            return null;
        }

        try {
            PortService.SP_DEVINFO_DATA deviceInfoData = new SP_DEVINFO_DATA();
            deviceInfoData.cbSize = deviceInfoData.size();

            int index = 0;
            while (ru.nozdratenko.sdpo.util.port.SetupApi.INSTANCE.SetupDiEnumDeviceInfo
                    (deviceInfoSet, index, deviceInfoData)) {
                index++;

                String currentDeviceInstanceId = getDeviceInstanceId(deviceInfoData);
                if (currentDeviceInstanceId != null && currentDeviceInstanceId.contains(vendorId)) {
                    ids.add(currentDeviceInstanceId);
                    SdpoLog.info("Found Device Instance Id: " + currentDeviceInstanceId);
                }
            }

            if (!ids.isEmpty()){
                if (ids.size() == 1) {
                    SdpoLog.info("Port is available for Device Instance Id: " + ids.get(0));
                    return ids.get(0);
                } else {// DEBATABLE code ...
                    if (AlcometerHelper.DEVICE_INSTANCE_ID != null){
                        SdpoLog.info("There are multiple choices for vendorId: " + vendorId);
                        return AlcometerHelper.DEVICE_INSTANCE_ID;
                    }
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

    private String getDeviceInstanceId(SP_DEVINFO_DATA deviceInfoData) {
        char[] buffer = new char[1024];
        int result = ru.nozdratenko.sdpo.util.port.Cfgmgr32.INSTANCE.CM_Get_Device_ID(deviceInfoData.DevInst, buffer, buffer.length, 0);
        if (result == Cfgmgr32.CR_SUCCESS) {
            return Native.toString(buffer);
        }
        return null;
    }

    private static final int TOKEN_QUERY = 0x0008;
    private static final int TokenElevation = 20;

    public static boolean isAdmin() {
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