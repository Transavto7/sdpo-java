package ru.nozdratenko.sdpo.task.Termometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ThermometerTaskRunner implements CommandLineRunner {
    private final ThreadPoolTaskExecutor taskExecutor;
    private final ThermometerResultTask thermometerResultTask;

    @Autowired
    public ThermometerTaskRunner(
            @Qualifier("thermometerTaskExecutor")
            ThreadPoolTaskExecutor taskExecutor,
            ThermometerResultTask thermometerResultTask
    ) {
        this.taskExecutor = taskExecutor;
        this.thermometerResultTask = thermometerResultTask;
    }

    public ThermometerResultTask getThermometerResultTask() {
        return thermometerResultTask;
    }

    @Override
    public void run(String... args) throws Exception {
        taskExecutor.execute(thermometerResultTask);
    }
}