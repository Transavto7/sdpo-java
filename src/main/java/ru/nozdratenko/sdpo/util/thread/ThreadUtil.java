package ru.nozdratenko.sdpo.util.thread;

public class ThreadUtil {
    public static void suspendCurrentAction (Long downtime){
        try {
            Thread.sleep(downtime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        }
    }
}
