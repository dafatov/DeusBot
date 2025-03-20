package ru.demetrious.deus.bot.adapter.output.shikimori.limiter;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.shikimori.ShikimoriClient;
import ru.demetrious.deus.bot.domain.limiter.Limiter;

@RequiredArgsConstructor
@Component
@Order(1)
public class RpsRateLimiter implements Limiter<ShikimoriClient> {
    @RateLimiter(name = "shikimori-rps")
    @Override
    public <R, A> Function<R, A> execute(Function<R, A> function) {
        return Limiter.super.execute(function);
    }
}
