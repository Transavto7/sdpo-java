package ru.nozdratenko.sdpo.util.port;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.util.port.PortService.PortService;

@Service
public class PortManager {
    private final PortService portService;

    @Autowired
    public PortManager(PortService portService) {
        this.portService = portService;
    }

    private static final String GUID_DEVINTERFACE_COMPORT = "86E0D1E0-8089-11D0-9CE4-08003E301F73";

    public boolean reinitializePort(String port) {
       return this.portService.reinitializePort(port);
    }

    public String getDeviceInstanceId(String vid) {
        return this.portService.getDeviceInstanceId(GUID_DEVINTERFACE_COMPORT, vid);
    }
}

