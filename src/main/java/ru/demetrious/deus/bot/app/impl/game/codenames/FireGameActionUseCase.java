package ru.demetrious.deus.bot.app.impl.game.codenames;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.codenames.FireGameActionInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.GetCodeNamesGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.codenames.NotifyGameErrorOutbound;
import ru.demetrious.deus.bot.app.api.game.codenames.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.api.Gamebox;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.Context;

import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.Context.Timer;
import static ru.demetrious.deus.bot.utils.JacksonUtils.writeValueAsString;

@Slf4j
@Component
public class FireGameActionUseCase implements FireGameActionInbound {
    private final Gamebox gamebox;
    private final NotifyGameStateOutbound notifyGameStateOutbound;
    private final NotifyGameErrorOutbound notifyGameErrorOutbound;
    private final ExecutorService virtualThreadPerTaskExecutor;
    private final Context context;

    public FireGameActionUseCase(Gamebox gamebox,
                                 NotifyGameStateOutbound notifyGameStateOutbound, NotifyGameErrorOutbound notifyGameErrorOutbound,
                                 ExecutorService virtualThreadPerTaskExecutor,
                                 GetCodeNamesGamePackWordsOutbound getCodeNamesGamePackWordsOutbound) {
        this.gamebox = gamebox;
        this.notifyGameStateOutbound = notifyGameStateOutbound;
        this.notifyGameErrorOutbound = notifyGameErrorOutbound;
        this.virtualThreadPerTaskExecutor = virtualThreadPerTaskExecutor;
        this.context = new Context(this::runTimerTask, getCodeNamesGamePackWordsOutbound);
    }

    @Override
    public void execute(String gameId, String userId, Action action) {
        log.debug("execute: gameId={}, userId={}, action={}", gameId, userId, writeValueAsString(action));
        GameSession gameSession = gamebox.getGameSession(gameId, userId);

        try {
            action.perform(gameSession, userId, context);
            notifyGameStateOutbound.notifyGameState(gameSession);
        } catch (Exception e) {
            log.warn(e.toString());
            notifyGameErrorOutbound.sendGameError(userId, e);
        }
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private void runTimerTask(@NotNull Timer timer) {
        State state = timer.gameSession().getState();
        Duration delay = timer.delay();
        Runnable realTask = () -> {
            state.setTimerTask(null);
            state.setTimerCompletableFuture(null);
            state.setTimer(null);
            timer.task().run();
            notifyGameStateOutbound.notifyGameState(timer.gameSession());
        };

        ofNullable(state.getTimerCompletableFuture()).ifPresent(f -> f.cancel(true));
        state.setTimer(now().plus(delay));
        state.setTimerCompletableFuture(runAsync(realTask, delayedExecutor(delay.toMillis(), MILLISECONDS, virtualThreadPerTaskExecutor)));
        state.setTimerTask(realTask);
        state.setRemaining(null);
    }
}
