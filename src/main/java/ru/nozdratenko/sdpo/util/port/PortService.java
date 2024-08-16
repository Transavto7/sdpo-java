package ru.nozdratenko.sdpo.util.port;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import ru.nozdratenko.sdpo.util.SdpoLog;

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

    public boolean reinitializePort(String deviceInstanceId) {
        try {
            String[] pnpCommandDisable = {"cmd.exe", "/c", String.format("PNPUTIL /disable-device \"%s\"", deviceInstanceId)};
            ProcessBuilder pnpPbDisable = new ProcessBuilder(pnpCommandDisable);
            Process pnpProcessDisable = pnpPbDisable.start();
            pnpProcessDisable.waitFor();

            String[] pnpCommand = {"cmd.exe", "/c", String.format("PNPUTIL /enable-device \"%s\"", deviceInstanceId)};
            ProcessBuilder pnpPb = new ProcessBuilder(pnpCommand);
            Process pnpProcess = pnpPb.start();
            pnpProcess.waitFor();

            String[] pnpCommandRestart = {"cmd.exe", "/c", String.format("PNPUTIL /restart-device \"%s\"", deviceInstanceId)};
            ProcessBuilder pnpPbRestart = new ProcessBuilder(pnpCommandRestart);
            Process pnpProcessRestart = pnpPbRestart.start();
            pnpProcessRestart.waitFor();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    String getDeviceInstanceId(String guidString, String vendorId) {
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

    private String getDeviceInstanceId(SP_DEVINFO_DATA deviceInfoData) {
        char[] buffer = new char[1024];
        int result = ru.nozdratenko.sdpo.util.port.Cfgmgr32.INSTANCE.CM_Get_Device_ID(deviceInfoData.DevInst, buffer, buffer.length, 0);
        if (result == Cfgmgr32.CR_SUCCESS) {
            String devId = Native.toString(buffer);
            return devId;
        }
        return null;
    }

}