package ru.demetrious.deus.bot.app.api.session;

@FunctionalInterface
public interface GuildVoiceSessionUpdateInbound {
    void execute(String guildId, String userId, boolean isJoined);
}
