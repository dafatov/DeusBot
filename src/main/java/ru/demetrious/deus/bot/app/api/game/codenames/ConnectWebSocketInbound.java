package ru.demetrious.deus.bot.app.api.game.codenames;

@FunctionalInterface
public interface ConnectWebSocketInbound {
    void execute(String userId);
}
