package ru.demetrious.deus.bot.app.api.game.codenames;

@FunctionalInterface
public interface JoinCodeNamesGameInbound {
    void execute(String gameId);
}
