package ru.nozdratenko.sdpo.Core.Network;

import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class PortWorker {
    private static final int PORT_TO_CHECK = 8080;

    public static Boolean isFree() {
        try (java.net.ServerSocket socket = new java.net.ServerSocket(PORT_TO_CHECK)) {
            socket.setReuseAddress(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Возвращает PID процесса, который занимает порт (Windows)
     */
    public static String getProcessIdOnPort(int port) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            return null;
        }

        List<String> command = List.of("cmd.exe", "/c", "netstat -ano");

        try {
            Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(":" + port + " ") && line.trim().startsWith("TCP")) {
                        SdpoLog.info("Процесс занявший порт:" + line);
                        String[] parts = line.trim().split("\\s+");
                        return parts[parts.length - 1];
                    }
                }
            }
        } catch (Exception e) {
            SdpoLog.error(e);
        }

        return null;
    }

    public static boolean killProcessOnPort(int port) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            return false;
        }

        String pid = getProcessIdOnPort(port);
        if (pid == null) {
            return false;
        }

        List<String> killCmd = List.of("cmd.exe", "/c", "taskkill", "/PID", pid, "/F");

        try {
            Process kill = new ProcessBuilder(killCmd)
                .redirectErrorStream(true)
                .start();

            int exit = kill.waitFor();
            return exit == 0;
        } catch (Exception e) {
            SdpoLog.error(e);
            return false;
        }
    }

    public static boolean killProcessOnPort() {
        return PortWorker.killProcessOnPort(PORT_TO_CHECK);
    }
}
