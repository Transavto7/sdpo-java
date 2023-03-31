package ru.nozdratenko.sdpo.task;

import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UpdateComPortTask extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) { }

            String thermometerPort = getComPort("VID_10C4");
            String alcometerPort = getComPort("VID_0483");

            if (thermometerPort == null || !thermometerPort.equals(ThermometerHelper.PORT)) {
                ThermometerHelper.PORT = thermometerPort;
                SdpoLog.info("Thermometer set port: " + ThermometerHelper.PORT);
            }

            if (alcometerPort == null) {
                AlcometerHelper.PORT = alcometerPort;
                SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
            } else if(!alcometerPort.equals(AlcometerHelper.PORT)) {
                AlcometerHelper.PORT = alcometerPort;
                SdpoLog.info("Alcometer set port: " + AlcometerHelper.PORT);
                AlcometerHelper.reset();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignore) { }
            }
        }
    }

    public static String getComPort(String vid) {
        String command = "powershell.exe  Get-PnpDevice -PresentOnly | Where-Object { $_.InstanceId -match '^USB\\\\" + vid + "' }";
        String result = null;

        try {
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();

            String line;
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                if (line.contains("(COM")) {
                    result = line.split("\\(COM")[1].substring(0, 1);
                    result = "COM" + result;
                }
            }
            stdout.close();

        } catch (IOException e) {
            SdpoLog.error("Not found com port " + vid + ": " + e);
        }

        return result;
    }
}
