package ru.demetrious.deus.bot.app.api.publicist;

@FunctionalInterface
public interface SetGuildPublicistOutbound {
    void setGuildPublicist(String guildId, String channelId);
}
