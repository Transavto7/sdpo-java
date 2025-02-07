package ru.nozdratenko.sdpo.util.port.PortService;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class SpDevinfoData extends Structure {
    public int cbSize = size();
    public GUID ClassGuid = new GUID();
    public int DevInst;
    public Pointer Reserved;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("cbSize", "ClassGuid", "DevInst", "Reserved");
    }
}