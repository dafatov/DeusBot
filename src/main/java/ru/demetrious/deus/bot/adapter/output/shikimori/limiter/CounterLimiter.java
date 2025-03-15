package ru.demetrious.deus.bot.adapter.output.shikimori.limiter;

import java.util.concurrent.atomic.AtomicInteger;
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
@Order(3)
public class CounterLimiter implements Limiter<ShikimoriClient> {
    private final static AtomicInteger counter = new AtomicInteger(0);

    @Override
    public <R, A> Function<R, A> execute(Function<R, A> function) {
        log.info("CounterLimiter: {}", counter.incrementAndGet());
        return Limiter.super.execute(function);
    }
}
