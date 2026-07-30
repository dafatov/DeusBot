package ru.demetrious.deus.bot.app.api.game;

@FunctionalInterface
public interface NotifyGameErrorOutbound {
    void notifyGameError(String userId, Exception exception);
}
