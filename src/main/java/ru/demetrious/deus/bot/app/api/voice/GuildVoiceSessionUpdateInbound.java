package ru.demetrious.deus.bot.app.api.voice;

@FunctionalInterface
public interface GuildVoiceSessionUpdateInbound {
    void execute(String guildId, String userId, boolean isJoined);
}
