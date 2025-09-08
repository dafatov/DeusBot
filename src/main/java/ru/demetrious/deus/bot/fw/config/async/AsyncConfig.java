package ru.demetrious.deus.bot.fw.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RequiredArgsConstructor
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private final AsyncProperties properties;

    @Bean
    public ThreadPoolTaskExecutor cacheWarmUpExecutor() {
        return createExecutor(properties.cacheWarmUp());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private ThreadPoolTaskExecutor createExecutor(AsyncProperties.ExecutorConfig config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(config.corePoolSize());
        executor.setMaxPoolSize(config.maxPoolSize());
        executor.setQueueCapacity(config.queueCapacity());
        executor.setThreadNamePrefix(config.threadNamePrefix());
        executor.setRejectedExecutionHandler(config.createRejectedHandler());
        executor.initialize();
        return executor;
    }
}