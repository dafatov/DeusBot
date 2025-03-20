package ru.demetrious.deus.bot.domain.limiter;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RateLimiterWrapper<T> {
    private final List<Limiter<T>> limiterList;

    public <R, A> Function<R, A> wrap(Function<R, A> function) {
        Function<R, A> tmp = function;

        for (Limiter<T> limiter : limiterList) {
            tmp = limiter.execute(tmp);
        }

        return tmp;
    }
}
