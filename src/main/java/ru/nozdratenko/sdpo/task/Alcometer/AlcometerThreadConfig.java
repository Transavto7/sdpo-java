package ru.nozdratenko.sdpo.task.Alcometer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AlcometerThreadConfig {
    @Bean
    public ThreadPoolTaskExecutor alcometerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setThreadNamePrefix("AlcometerThread-");
        executor.initialize();
        return executor;
    }
}