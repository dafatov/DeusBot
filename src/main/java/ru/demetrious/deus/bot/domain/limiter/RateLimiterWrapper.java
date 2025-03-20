package ru.demetrious.deus.bot.domain.limiter;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RateLimiterWrapper<T> {
    //private final List<Limiter<T>> limiterList;

    public <A, R> Function<A, R> wrap(Function<A, R> function) {
        return function;
    }
    /*public <A, R> Function<A, R> wrap(Function<A, R> function) {
        return f -> {
            try {
                log.info("called in {}", Instant.now());
                return call(f, function);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }*/

    @RateLimiter(name = "test")
    @Bulkhead(name = "test")
    public static <R, A> R call(A arg, Function<A, R> function) {
        return function.apply(arg);
    }
}
