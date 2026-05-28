package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.Context.Timer;

import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.Objects.isNull;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.GUESSING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.HINTING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkPhase;

@Builder
public record SetPauseAction() implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, userId);
        checkPhase(gameSession, GUESSING, HINTING);

        if (isNull(gameSession.getState().getRemaining())) {
            gameSession.getState().setRemaining(between(now(), gameSession.getState().getTimer()));
            gameSession.getState().getTimerCompletableFuture().cancel(true);
            gameSession.getState().setTimer(null);
        } else {
            ctx.timerSetter().accept(new Timer(gameSession, gameSession.getState().getRemaining(), gameSession.getState().getTimerTask()));
        }
    }
}
