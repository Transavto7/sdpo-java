package ru.nozdratenko.sdpo.util.port;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Cfgmgr32 extends StdCallLibrary {
    Cfgmgr32 INSTANCE = Native.load("cfgmgr32", Cfgmgr32.class, W32APIOptions.DEFAULT_OPTIONS);

    int CR_SUCCESS = 0;

    int CM_Get_Device_ID(
            int dnDevInst,
            char[] Buffer,
            int BufferLen,
            int ulFlags
    );
}
