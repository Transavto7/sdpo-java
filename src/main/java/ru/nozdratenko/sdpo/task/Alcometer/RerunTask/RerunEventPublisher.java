package ru.nozdratenko.sdpo.task.Alcometer.RerunTask;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class RerunEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public RerunEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish() {
        RerunAlcometerTaskEvent event = new RerunAlcometerTaskEvent(this);
        eventPublisher.publishEvent(event);
    }
}
