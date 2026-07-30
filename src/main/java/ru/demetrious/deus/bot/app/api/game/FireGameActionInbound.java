package ru.demetrious.deus.bot.app.api.game;

import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;

@FunctionalInterface
public interface FireGameActionInbound {
    void execute(String gameId, String userId, Action<?, ?, ?, ?> action);
}
