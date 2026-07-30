package ru.demetrious.deus.bot.app.impl.game.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.api.game.GetGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@Slf4j
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "game")
@JsonSubTypes({
    @Type(value = CodeNamesActionContext.class, name = CODE_NAMES),
    @Type(value = CrossWardActionContext.class, name = CROSS_WARD),
})
@RequiredArgsConstructor
public abstract class ActionContext<S extends Setting, P extends Player, G extends Instance<S, P>> {
    @Getter
    private final GetGamePackWordsOutbound dictionary;
    private final NotifyGameStateOutbound notifyGameStateOutbound;
    private final ExecutorService virtualThreadPerTaskExecutor;

    public void startTimer(G gameSession, Duration delay, Runnable task) {
        Timer timer = gameSession.getTimer();

        timer.setTask(() -> {
            cancelTimer(timer);
            task.run();
            notifyGameStateOutbound.notifyGameState(gameSession);
        });
        timer.setFinish(now().plus(delay));
        timer.setRemaining(null);
        timer.setFuture(runAsync(timer.getTask(), delayedExecutor(delay.toMillis(), MILLISECONDS, virtualThreadPerTaskExecutor)));
        log.trace("Timer started: {}", timer);
    }

    public void pauseTimer(Timer timer) throws ActionException {
        if (isNull(timer.getFuture()) || timer.getFuture().isDone() || isNull(timer.getFinish())) {
            throw new ActionException("Can't pause timer because timer has no future or finish, or completed");
        }

        timer.setRemaining(between(now(), timer.getFinish()));
        timer.getFuture().cancel(true);
        timer.setFuture(null);
        timer.setFinish(null);
        log.trace("Timer paused: {}", timer);
    }

    public void resumeTimer(G gameSession) throws ActionException {
        Timer timer = gameSession.getTimer();

        if (isNull(timer.getRemaining()) || isNull(timer.getTask())) {
            throw new ActionException("Can't resume timer because has no remaining or task");
        }

        startTimer(gameSession, timer.getRemaining(), timer.getTask());
        log.trace("Timer resumed: {}", timer);
    }

    public void cancelTimer(Timer timer) {
        ofNullable(timer.getFuture()).ifPresent(f -> f.cancel(true));
        timer.setTask(null);
        timer.setFinish(null);
        timer.setRemaining(null);
        timer.setFuture(null);
        log.trace("Timer canceled: {}", timer);
    }

    public void extendTimer(G gameSession, Duration extra) throws ActionException {
        Timer timer = gameSession.getTimer();

        if (timer.getFuture() == null || timer.getFuture().isDone() || timer.getFinish() == null) {
            throw new ActionException("Cannot extend timer because it is not running");
        }

        startTimer(gameSession, between(now(), timer.getFinish()).plus(extra), timer.getTask());
        log.trace("Timer extend: {}", timer);
    }
}
