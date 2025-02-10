package ru.nozdratenko.sdpo.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.event.StopRunProcessesEvent;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;
import ru.nozdratenko.sdpo.util.device.BluetoothDeviceService;

@Component
public class ProcessCloserListener {

    @Async
    @EventListener
    public void handleStopRunProcessesEvent(StopRunProcessesEvent event) {
        SdpoLog.info("!!! Current tonom status: " + Sdpo.tonometerResultTask.currentStatus);
        if (!Sdpo.tonometerResultTask.currentStatus.equals(StatusType.FREE)) {
            BluetoothDeviceService.stopPreviousTonometerApp();
            Sdpo.tonometerResultTask.currentStatus = StatusType.STOP;
        }
    }
}
