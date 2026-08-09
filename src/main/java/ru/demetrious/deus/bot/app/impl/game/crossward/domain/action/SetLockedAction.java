package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkHost;

@Builder
public record SetLockedAction() implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, String userId, CrossWardActionContext ctx) throws ActionException {
        checkHost(gameSession, userId);

        gameSession.getState().setLocked(!gameSession.getState().isLocked());
    }
}
