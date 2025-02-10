package ru.nozdratenko.sdpo.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.event.StopRunProcessesEvent;
import ru.nozdratenko.sdpo.task.Tonometer.TonometerResultTask;
import ru.nozdratenko.sdpo.task.Tonometer.TonometerTaskRunner;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;
import ru.nozdratenko.sdpo.util.device.BluetoothDeviceService;

@Component
public class ProcessCloserListener {
    private final TonometerTaskRunner tonometerTaskRunner;

    @Autowired
    public ProcessCloserListener(TonometerTaskRunner tonometerTaskRunner) {
        this.tonometerTaskRunner = tonometerTaskRunner;
    }

    @Async
    @EventListener
    public void handleStopRunProcessesEvent(StopRunProcessesEvent event) {
        TonometerResultTask tonometerResultTask = this.tonometerTaskRunner.getTonometerResultTask();
        SdpoLog.info("!!! Current tonom status: " + tonometerResultTask.getCurrentStatus());
        if (!tonometerResultTask.getCurrentStatus().equals(StatusType.FREE)) {
            BluetoothDeviceService.stopPreviousTonometerApp();
            tonometerResultTask.setCurrentStatus(StatusType.STOP);
        }
    }
}
