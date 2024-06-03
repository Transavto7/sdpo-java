package ru.nozdratenko.sdpo.storage;

import java.io.IOException;

public interface RemoteServerRequest {

    public void loadApi(String url) throws IOException;

}
