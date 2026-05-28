package ru.demetrious.deus.bot.fw.config.async;

import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import static java.lang.Thread.ofVirtual;
import static java.util.concurrent.Executors.newThreadPerTaskExecutor;

@RequiredArgsConstructor
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Bean(name = "virtualThreadPerTaskExecutor")
    public ExecutorService virtualThreadPerTaskExecutor() {
        return newThreadPerTaskExecutor(ofVirtual().name("virtual-thread-per-task-executor-", 0).factory());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}