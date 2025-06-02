package ru.nozdratenko.sdpo.task.Alcometer;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Component
public class AlcometerTaskRunner implements CommandLineRunner {
    private final ThreadPoolTaskExecutor taskExecutor;
    @Getter
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

    @Override
    public void run(String... args) throws Exception {
        if (!Sdpo.isInitialized()) {
            return;
        }
        SdpoLog.info("!!!!!!! Alcometer run task: ");

        taskExecutor.execute(alcometerResultTask);
    }

    public void startAlcometerResultTask(){
        SdpoLog.info("!!!!!!! startAlcometerResultTask: ");

        taskExecutor.execute(alcometerResultTask);
    }
}