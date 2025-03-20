package ru.demetrious.deus.bot.adapter.output.shikimori.limiter;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.shikimori.ShikimoriClient;
import ru.demetrious.deus.bot.domain.limiter.Limiter;

@RequiredArgsConstructor
@Component
@Order(2)
public class RpmRateLimiter implements Limiter<ShikimoriClient> {
    @RateLimiter(name = "shikimori-rpm")
    @Retry(name = "shikimori-rpm")
    @Override
    public <R, A> Function<R, A> execute(Function<R, A> function) {
        return Limiter.super.execute(function);
    }
}
