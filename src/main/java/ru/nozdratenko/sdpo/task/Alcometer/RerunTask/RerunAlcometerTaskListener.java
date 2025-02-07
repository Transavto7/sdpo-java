package ru.nozdratenko.sdpo.task.Alcometer.RerunTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.task.Alcometer.AlcometerTaskRunner;
import ru.nozdratenko.sdpo.util.SdpoLog;

@Component
public class RerunAlcometerTaskListener implements ApplicationListener<RerunAlcometerTaskEvent> {
    private final AlcometerTaskRunner alcometerTaskRunner;

    @Autowired
    public RerunAlcometerTaskListener(AlcometerTaskRunner alcometerTaskRunner) {
        this.alcometerTaskRunner = alcometerTaskRunner;
    }

    @Override
    public void onApplicationEvent(RerunAlcometerTaskEvent event) {
        alcometerTaskRunner.getAlcometerResultTask().stop();
        SdpoLog.info("Rerun alcometer task ...");
        alcometerTaskRunner.startAlcometerResultTask();
    }
}
