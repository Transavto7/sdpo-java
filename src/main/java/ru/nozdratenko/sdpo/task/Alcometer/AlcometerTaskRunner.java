package ru.nozdratenko.sdpo.task.Alcometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.util.SdpoLog;

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
        SdpoLog.info("!!!!!!! Alcometer run task: ");

        taskExecutor.execute(alcometerResultTask);
    }

    public void startAlcometerResultTask(){
        SdpoLog.info("!!!!!!! Alcometer startAlcometerResultTask task: ");

        taskExecutor.execute(alcometerResultTask);
    }
}