package ru.demetrious.deus.bot.app.api.game.codenames;

import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action;

@FunctionalInterface
public interface FireGameActionInbound {
    void execute(String gameId, String userId, Action action);
}
