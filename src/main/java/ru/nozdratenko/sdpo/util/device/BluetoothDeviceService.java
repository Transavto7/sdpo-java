package ru.nozdratenko.sdpo.util.device;

import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BluetoothDeviceService {
    private static String workDir = null;
    private static final String TonometerApp = "bleapp.exe";
    private static final String getTonometerResult = "getTonometerResult";
    private static final String tonometer_pid = "tonometer_process_id";

    public static List<String> getTonometerResult() {
        try {
            String[] command = {workDir, getTonometerResult};

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            savePid(process.pid());

            // Read the output from the C# application
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() && output.length() > 0) {
                    output.append("\n");
                }
                output.append(line);
            }

            // Wait for the process to exit
            int exitCode = process.waitFor();
            SdpoLog.info("Tonometer App exited with code: " + exitCode);

            return processBleappResult(output.toString());
        } catch (Exception e) {
            SdpoLog.error(e.getMessage());
        }
        return null;
    }

    private static List<String> processBleappResult(String tonomResult){
        String[] resultArr = tonomResult.split("#");
        return List.of(resultArr[0], resultArr.length > 1 ? resultArr[1] : "");
    }

    public static void start() {
        try {
            stopPreviousTonometerApp();
            Thread.sleep(2000);
            workDir = FileBase.exportLibrary(TonometerApp);
            SdpoLog.info("Tonometer App dir: " + workDir);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void savePid(long pid) {
        Sdpo.settings.systemConfig.set(tonometer_pid, (int) pid).saveFile();
        SdpoLog.info("New tonometer PID: " + pid);
    }

    public static void stopPreviousTonometerApp() {
        try {
            int PID = Sdpo.settings.systemConfig.getInt(tonometer_pid);
            if (PID != 0) {
                ProcessHandle processHandle = ProcessHandle.of(PID).orElse(null);
                if (processHandle != null && processHandle.isAlive()) {
                    SdpoLog.info("Stop previous tonometer process with PID: " + PID);
                    processHandle.destroy();
                    processHandle.onExit().get(5, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
