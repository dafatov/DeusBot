package ru.demetrious.deus.bot.fw.config.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.fw.annotation.cache.InitWarmUp;

import static java.util.Objects.isNull;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.doWithMethods;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheWarmUpRunner implements ApplicationRunner {
    private final ApplicationContext applicationContext;
    private final ThreadPoolTaskExecutor cacheWarmUpExecutor;

    @Override
    public void run(ApplicationArguments args) {
        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        for (Object bean : applicationContext.getBeansOfType(Object.class).values()) {
            doWithMethods(ultimateTargetClass(bean), method -> {
                InitWarmUp initWarmUp = findAnnotation(method, InitWarmUp.class);
                Cacheable cacheable = findAnnotation(method, Cacheable.class);

                if (isNull(initWarmUp) || isNull(cacheable)) {
                    return;
                }

                tasks.add(runAsync(() -> {
                    try {
                        log.info("Starting warm up for {}", Arrays.toString(cacheable.cacheNames()));
                        method.invoke(bean);
                        log.info("Finished warm up for {}", Arrays.toString(cacheable.cacheNames()));
                    } catch (Exception e) {
                        log.error("Can't init warm up for {}", Arrays.toString(cacheable.cacheNames()), e);
                    }
                }, cacheWarmUpExecutor));
            });
        }

        allOf(tasks.toArray(CompletableFuture[]::new)).join();
    }
}
