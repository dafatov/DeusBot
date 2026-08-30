package ru.demetrious.deus.bot.app.api.game;

import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;

@FunctionalInterface
public interface NotifyGameStateOutbound {
    void notifyGameState(Instance<?, ?> gameSession);
}
