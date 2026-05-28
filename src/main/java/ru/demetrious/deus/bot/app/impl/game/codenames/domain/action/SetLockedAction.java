package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkHost;

@Builder
public record SetLockedAction() implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) throws ActionException {
        checkHost(gameSession, userId);

        gameSession.getState().setLocked(!gameSession.getState().isLocked());
    }
}
