package ru.demetrious.deus.bot.app.api.game;

@FunctionalInterface
public interface DisconnectWebSocketInbound {
    void execute(String userId);
}
