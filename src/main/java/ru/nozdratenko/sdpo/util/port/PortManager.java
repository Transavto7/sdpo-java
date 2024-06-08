package ru.nozdratenko.sdpo.util.port;

public class PortManager {

    private static PortService portService = new PortService();

    private static final String GUID_DEVINTERFACE_COMPORT = "86E0D1E0-8089-11D0-9CE4-08003E301F73";

    public static boolean reinitializePort(String port) {
       return portService.reinitializePort(port);
    }

    public static String getDeviceInstanceId(String vid) {
        return portService.getDeviceInstanceId(GUID_DEVINTERFACE_COMPORT, vid);
    }
}

