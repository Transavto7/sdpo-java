package ru.nozdratenko.sdpo.util.port.PortService;

public interface PortService {
    boolean reinitializePort(String deviceInstanceId);
    String getDeviceInstanceId(String guidString, String vendorId);
    boolean isAdmin();
}