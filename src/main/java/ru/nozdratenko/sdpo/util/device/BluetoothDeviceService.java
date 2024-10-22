package ru.nozdratenko.sdpo.util.device;

import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.file.FileBase;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class BluetoothDeviceService {
    private static String workDir = null;
    private static final String BleApp = "bleapp.exe";
    private static final String getTonometerResult = "getTonometerResult";
    private static final String tonometer_process_id = "tonometer_process_id";
    private static final Integer PID = Sdpo.systemConfig.getInt(tonometer_process_id);

    public static String getTonometerResult() {
        try {
            String[] command = {workDir, getTonometerResult};

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            long pid = process.pid();
            savePid(pid);

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
            SdpoLog.info("BleApp exited with code: " + exitCode);

            return output.toString();
        } catch (Exception e) {
            SdpoLog.error(e.getMessage());
        }
        return null;
    }

    public static void start() {
        try {
            stopPreviousTonometerApp();
            Thread.sleep(2000);
            workDir = FileBase.exportLibrary(BleApp);
            SdpoLog.info("bleapp.exe placed into: " + workDir);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void savePid(long pid) {
        Sdpo.systemConfig.set(tonometer_process_id, (int) pid).saveFile();
        SdpoLog.info("Saved tonometer_process_id: " + pid);
    }

    public static void stopPreviousTonometerApp() {
        try {
            if (PID != 0) {
                SdpoLog.info("Previous bleapp PID: " + PID);
                ProcessHandle processHandle = ProcessHandle.of(PID).orElse(null);
                if (processHandle != null && processHandle.isAlive()) {
                    SdpoLog.info("Stopping previous bleapp process with PID: " + PID);
                    processHandle.destroy();
                    processHandle.onExit().get(5, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
