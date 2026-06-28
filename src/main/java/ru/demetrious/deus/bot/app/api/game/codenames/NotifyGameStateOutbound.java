package ru.demetrious.deus.bot.app.api.game.codenames;

import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;

@FunctionalInterface
public interface NotifyGameStateOutbound {
    void notifyGameState(GameSession gameSession);
}
