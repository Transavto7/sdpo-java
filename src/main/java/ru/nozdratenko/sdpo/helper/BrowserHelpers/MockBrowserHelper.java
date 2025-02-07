package ru.nozdratenko.sdpo.helper.BrowserHelpers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Profile("develop")
public class MockBrowserHelper implements BrowserHelper {
    public void openUrl(String url) {
       SdpoLog.info("MockBrowserHelper::openUrl");
    }
}
