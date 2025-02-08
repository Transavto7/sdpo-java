package ru.nozdratenko.sdpo.lib.COMPortsServices;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.lib.COMPorts;

@Component
@Profile("production")
public class WindowsCOMPorts implements ru.nozdratenko.sdpo.lib.COMPortsServices.COMPorts {
    public String getComPort(String vid)
    {
        return COMPorts.getComPort(vid);
    }
}
