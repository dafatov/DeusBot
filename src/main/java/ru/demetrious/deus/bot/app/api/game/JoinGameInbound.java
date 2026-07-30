package ru.demetrious.deus.bot.app.api.game;

@FunctionalInterface
public interface JoinGameInbound {
    void execute(String gameId);
}
