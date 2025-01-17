package ru.demetrious.deus.bot.app.impl.player.api;

@FunctionalInterface
public interface Jukebox {
    Player getPlayer(String guildId);
}
