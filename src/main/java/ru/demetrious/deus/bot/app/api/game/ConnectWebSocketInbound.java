package ru.demetrious.deus.bot.app.api.game;

@FunctionalInterface
public interface ConnectWebSocketInbound {
    void execute(String userId);
}
