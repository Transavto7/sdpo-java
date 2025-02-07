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
@Profile("develop")
public class MockPortService implements PortService {
    public boolean reinitializePort(String deviceInstanceId) {
        SdpoLog.info("MockPortService::reinitializePort");
        return false;
    }

    public String getDeviceInstanceId(String guidString, String vendorId) {
        SdpoLog.info("getDeviceInstanceId: Device with vendorId " + vendorId + " not found");
        SdpoLog.info("MockPortService::getDeviceInstanceId");
        return null;
    }

    public boolean isAdmin() {
        SdpoLog.info("MockPortService::isAdmin");
        return true;
    }
}