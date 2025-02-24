package ru.nozdratenko.sdpo.task.Tonometer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TonometerThreadConfig {
    @Bean(name = "tonometerTaskExecutor")
    public ThreadPoolTaskExecutor tonometerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setThreadNamePrefix("TonometerThread-");
        executor.initialize();
        return executor;
    }
}