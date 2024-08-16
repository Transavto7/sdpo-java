package ru.nozdratenko.sdpo.util.port;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface SetupApi extends StdCallLibrary {
    SetupApi INSTANCE = Native.load("setupapi", SetupApi.class, W32APIOptions.DEFAULT_OPTIONS);

    WinNT.HANDLE SetupDiGetClassDevs(
            PortService.GUID guid,
            String enumerator,
            WinDef.HWND hwndParent,
            int flags
    );

    boolean SetupDiDestroyDeviceInfoList(WinNT.HANDLE hDevInfo);

    boolean SetupDiEnumDeviceInfo(
            WinNT.HANDLE deviceInfoSet,
            int memberIndex,
            PortService.SP_DEVINFO_DATA deviceInfoData
    );

}

