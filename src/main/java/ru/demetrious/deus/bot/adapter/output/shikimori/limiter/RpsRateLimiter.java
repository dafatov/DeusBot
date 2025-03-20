package ru.demetrious.deus.bot.adapter.output.shikimori.limiter;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.shikimori.ShikimoriClient;
import ru.demetrious.deus.bot.domain.limiter.Limiter;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class RpsRateLimiter implements Limiter<ShikimoriClient> {
    @RateLimiter(name = "shikimori-rps")
    @Retry(name = "shikimori-rps")
    @Override
    public <R, A> Function<R, A> execute(Function<R, A> function) {
        log.info("RpsRateLimiter worked");
        return Limiter.super.execute(function);
    }
}
