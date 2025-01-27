package ru.demetrious.deus.bot.app.api.player;

@FunctionalInterface
public interface DisconnectOutbound {
    void disconnect(String guildId);
}
