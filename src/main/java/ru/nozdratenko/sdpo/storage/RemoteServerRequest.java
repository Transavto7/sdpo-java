package ru.nozdratenko.sdpo.storage;

import java.io.IOException;

public interface RemoteServerRequest {

    public void loadFromApi() throws IOException;

}
