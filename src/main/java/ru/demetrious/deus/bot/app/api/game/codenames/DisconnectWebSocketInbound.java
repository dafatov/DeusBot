package ru.demetrious.deus.bot.app.api.game.codenames;

@FunctionalInterface
public interface DisconnectWebSocketInbound {
    void execute(String userId);
}
