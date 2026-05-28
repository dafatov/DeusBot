package ru.demetrious.deus.bot.app.api.game.codenames;

@FunctionalInterface
public interface NotifyGameErrorOutbound {
    void sendGameError(String userId, Exception exception);
}
