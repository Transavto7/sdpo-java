package ru.nozdratenko.sdpo.lib;

import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

public class COMPorts {
    static {
        try {
            String url = FileBase.exportLibrary("com-win-cpp.dll");
            System.load(url.replace("\\", "/"));
            SdpoLog.info("com-win-cpp.dll loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            SdpoLog.error("Error loading DLL: " + e.getMessage());
            throw e;
        }
    }

    public static native String getComPort(String vid);
}
