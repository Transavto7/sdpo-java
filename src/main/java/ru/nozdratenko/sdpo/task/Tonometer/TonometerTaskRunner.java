package ru.nozdratenko.sdpo.task.Tonometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Component
public class TonometerTaskRunner implements CommandLineRunner {
    private final ThreadPoolTaskExecutor taskExecutor;
    private final TonometerConnectTask tonometerConnectTask;
    private final TonometerResultTask tonometerResultTask;

    @Autowired
    public TonometerTaskRunner(
            @Qualifier("tonometerTaskExecutor")
            ThreadPoolTaskExecutor taskExecutor,
            TonometerConnectTask tonometerConnectTask,
            TonometerResultTask tonometerResultTask
    ) {
        this.taskExecutor = taskExecutor;
        this.tonometerConnectTask = tonometerConnectTask;
        this.tonometerResultTask = tonometerResultTask;
    }

    public TonometerResultTask getTonometerResultTask() {
        return tonometerResultTask;
    }

    public TonometerConnectTask getTonometerConnectTask() {
        return tonometerConnectTask;
    }

    @Override
    public void run(String... args) throws Exception {
        taskExecutor.execute(tonometerConnectTask);
        taskExecutor.execute(tonometerResultTask);
    }
}