package ru.demetrious.deus.bot.domain.limiter;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.utils.JacksonUtils;

import static io.github.resilience4j.core.IntervalFunction.ofExponentialRandomBackoff;
import static io.github.resilience4j.ratelimiter.RateLimiter.decorateFunction;
import static io.github.resilience4j.retry.Retry.decorateFunction;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
@Component
public class RateLimiterWrapper<T> {
    private static final RateLimiterRegistry LIMITER_REGISTRY = RateLimiterRegistry.custom()
        .addRateLimiterConfig("rps", RateLimiterConfig.custom()
            .limitForPeriod(4)
            .limitRefreshPeriod(ofSeconds(1))
            .timeoutDuration(ofSeconds(2))
            .build())
        .addRateLimiterConfig("rpm", RateLimiterConfig.custom()
            .limitForPeriod(72)
            .limitRefreshPeriod(ofMinutes(1))
            .timeoutDuration(ofMinutes(2))
            .drainPermissionsOnResult(either -> {
                try {
                    JacksonUtils.getMapper().writeValueAsString(either);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return false;
            })
            .build())
        .build();
    //private final List<Limiter<T>> limiterList;

    public <R, A> Function<R, A> wrap(Function<R, A> function) {
        RetryRegistry retryRegistry = RetryRegistry.custom()
            .addRetryConfig("retry", RetryConfig.custom()
                .maxAttempts(6)
                .intervalFunction(ofExponentialRandomBackoff(ofSeconds(2), 2, ofMinutes(5)))
                .failAfterMaxAttempts(true)
                .build())
            .build();

        return decorateFunction(
            retryRegistry.retry("retry"), decorateFunction(
                LIMITER_REGISTRY.rateLimiter("rpm"), decorateFunction(
                    LIMITER_REGISTRY.rateLimiter("rps"), function)));
    }
}
