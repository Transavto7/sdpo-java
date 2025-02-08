package ru.nozdratenko.sdpo.task.Termometer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThermometerThreadConfig {
    @Bean
    public ThreadPoolTaskExecutor thermometerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setThreadNamePrefix("TermometerThread-");
        executor.initialize();
        return executor;
    }
}