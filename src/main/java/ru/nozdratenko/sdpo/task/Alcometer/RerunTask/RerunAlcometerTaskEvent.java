package ru.nozdratenko.sdpo.task.Alcometer.RerunTask;

import org.springframework.context.ApplicationEvent;

public class RerunAlcometerTaskEvent extends ApplicationEvent {
    public RerunAlcometerTaskEvent(Object source) {
        super(source);
    }
}
