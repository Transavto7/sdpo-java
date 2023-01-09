package ru.nozdratenko.sdpo.helper;

import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserHelper {

    public static void openUrl(String url) {
        String cmd = Sdpo.mainConfig.getString("run_browser_cmd");
        if (cmd != null && !cmd.isEmpty()) {
            Runtime runtime = Runtime.getRuntime();
            try {
                SdpoLog.info("Open browser: " + url.toString());
                Process proccess = runtime.exec(cmd.replace("${url}", url));

                if (Sdpo.mainConfig.getBoolean("browser_closed")) {
                    new Thread(()->{
                        while (true) {
                            if (!proccess.isAlive()) {
                                SdpoLog.info("Exit program close browser");
                                System.exit(0);
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) { }
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
                SdpoLog.info("Open browser: " + url.toString());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                SdpoLog.info("Open browser: " + url.toString());
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
