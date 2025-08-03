package ru.nozdratenko.sdpo.helper.AdminHelpers;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Component
@Profile("production")
public class WindowsAdminHelper implements AdminHelper {
    private static final int TOKEN_QUERY = 0x0008;
    private static final int TokenElevation = 20;

    public boolean isAdmin() {
        boolean viaNetSession = this.isAdminByNetSession();
        boolean viaTokenElevation = this.isAdminByTokenElevation();
        if (!viaNetSession) {
            SdpoLog.error("Net session check isAdmin failed.");
        }
        if (!viaTokenElevation) {
            SdpoLog.error("Token elevation check isAdmin failed.");
        }
        return viaNetSession && viaTokenElevation;
    }

    public boolean isAdminByNetSession() {
        try {
            Process process = new ProcessBuilder("net", "session").start();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAdminByTokenElevation() {
        WinNT.HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
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
