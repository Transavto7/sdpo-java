package ru.nozdratenko.sdpo.lib.COMPortsServices;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("develop")
public class MockCOMPorts implements COMPorts {
    public String getComPort(String vid){
        return "testing";
    }
}
