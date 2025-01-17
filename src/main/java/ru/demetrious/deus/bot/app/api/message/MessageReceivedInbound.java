package ru.demetrious.deus.bot.app.api.message;

public interface MessageReceivedInbound {
    void execute(String guildId, String userId);
}
