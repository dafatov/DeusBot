package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;

import static java.util.Objects.isNull;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.PLAYING;

@Builder
public record SetPauseAction() implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, String userId, CrossWardActionContext ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, userId);
        checkPhase(gameSession, PLAYING);

        if (isNull(gameSession.getTimer().getRemaining())) {
            ctx.pauseTimer(gameSession.getTimer());
        } else {
            ctx.resumeTimer(gameSession);
        }
    }
}
