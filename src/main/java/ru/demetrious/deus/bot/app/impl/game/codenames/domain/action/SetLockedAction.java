package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkHost;

@Builder
public record SetLockedAction() implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, String userId, CodeNamesActionContext ctx) throws ActionException {
        checkHost(gameSession, userId);

        gameSession.getState().setLocked(!gameSession.getState().isLocked());
    }
}
