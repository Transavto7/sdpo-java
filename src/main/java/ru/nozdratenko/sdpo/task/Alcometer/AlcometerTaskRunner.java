package ru.nozdratenko.sdpo.task.Alcometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AlcometerTaskRunner implements CommandLineRunner {
    private final ThreadPoolTaskExecutor taskExecutor;
    private final AlcometerResultTask alcometerResultTask;

    @Autowired
    public AlcometerTaskRunner(
            @Qualifier("alcometerTaskExecutor")
            ThreadPoolTaskExecutor taskExecutor,
            AlcometerResultTask alcometerResultTask
    ) {
        this.taskExecutor = taskExecutor;
        this.alcometerResultTask = alcometerResultTask;
    }

    public AlcometerResultTask getAlcometerResultTask() {
        return alcometerResultTask;
    }

    @Override
    public void run(String... args) throws Exception {
        taskExecutor.execute(alcometerResultTask);
    }

    public void startAlcometerResultTask(){
        taskExecutor.execute(alcometerResultTask);
    }
}