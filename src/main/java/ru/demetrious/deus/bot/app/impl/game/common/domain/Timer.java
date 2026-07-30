package ru.demetrious.deus.bot.app.impl.game.common.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Data
public class Timer {
    @ToString.Exclude
    private Runnable task;
    private Instant finish;
    private Duration remaining;
    @ToString.Exclude
    private CompletableFuture<Void> future;
}
