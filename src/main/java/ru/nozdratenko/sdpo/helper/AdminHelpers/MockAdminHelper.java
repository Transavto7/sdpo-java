package ru.nozdratenko.sdpo.helper.AdminHelpers;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Component
@Profile("develop")
public class MockAdminHelper implements AdminHelper {
    public boolean isAdmin() {
        return true;
    }

    public boolean isAdminByNetSession() {
        return true;
    }
}
