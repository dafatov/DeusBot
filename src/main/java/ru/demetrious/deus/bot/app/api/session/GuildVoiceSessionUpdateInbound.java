package ru.demetrious.deus.bot.app.api.session;

public interface GuildVoiceSessionUpdateInbound {
    void execute(String guildId, String userId, boolean isJoined);

    void execute(String guildId, String userId, boolean isJoined, boolean isForced);
}
