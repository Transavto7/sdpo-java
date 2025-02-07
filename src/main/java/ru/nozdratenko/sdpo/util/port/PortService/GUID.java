package ru.nozdratenko.sdpo.util.port.PortService;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GUID extends Structure {
    public int Data1;
    public short Data2;
    public short Data3;
    public byte[] Data4 = new byte[8];

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("Data1", "Data2", "Data3", "Data4");
    }

    public GUID() {
    }

    public GUID(String guidStr) {
        UUID uuid = UUID.fromString(guidStr);
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        Data1 = (int) (msb >> 32);
        Data2 = (short) (msb >> 16);
        Data3 = (short) msb;
        for (int i = 0; i < 8; i++) {
            Data4[i] = (byte) (lsb >> (8 * (7 - i)));
        }
    }
}
