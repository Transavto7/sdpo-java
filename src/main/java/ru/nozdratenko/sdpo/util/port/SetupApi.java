package ru.nozdratenko.sdpo.util.port;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import ru.nozdratenko.sdpo.util.port.PortService.GUID;
import ru.nozdratenko.sdpo.util.port.PortService.SpDevinfoData;

public interface SetupApi extends StdCallLibrary {
    SetupApi INSTANCE = Native.load("setupapi", SetupApi.class, W32APIOptions.DEFAULT_OPTIONS);

    WinNT.HANDLE SetupDiGetClassDevs(
            GUID guid,
            String enumerator,
            WinDef.HWND hwndParent,
            int flags
    );

    boolean SetupDiDestroyDeviceInfoList(WinNT.HANDLE hDevInfo);

    boolean SetupDiEnumDeviceInfo(
            WinNT.HANDLE deviceInfoSet,
            int memberIndex,
            SpDevinfoData deviceInfoData
    );

}

