package ru.demetrious.deus.bot.app.api.player;

@FunctionalInterface
public interface ClearGuildPlayerInbound {
    void execute(String guildId);
}
