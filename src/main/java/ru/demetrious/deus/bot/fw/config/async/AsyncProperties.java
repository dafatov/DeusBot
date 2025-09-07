package ru.demetrious.deus.bot.fw.config.async;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "async.executor")
public record AsyncProperties(ExecutorConfig cacheWarmUp) {
    public record ExecutorConfig(String threadNamePrefix,
                                 int corePoolSize,
                                 int maxPoolSize,
                                 int queueCapacity,
                                 RejectedPolicy rejectedPolicy) {
        public RejectedExecutionHandler createRejectedHandler() {
            return rejectedPolicy.getFactory().get();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum RejectedPolicy {
        ABORT(AbortPolicy::new),
        DISCARD(DiscardPolicy::new),
        DISCARD_OLDEST(DiscardOldestPolicy::new),
        CALLER_RUNS(CallerRunsPolicy::new);

        private final Supplier<RejectedExecutionHandler> factory;
    }
}