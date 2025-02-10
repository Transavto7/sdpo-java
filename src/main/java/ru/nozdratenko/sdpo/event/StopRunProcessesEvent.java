package ru.nozdratenko.sdpo.event;

import org.springframework.context.ApplicationEvent;

public class StopRunProcessesEvent  extends ApplicationEvent {
    public StopRunProcessesEvent(Object source) {
        super(source);
    }
}