package ru.demetrious.deus.bot.app.api.voice;

@FunctionalInterface
public interface AskByVoiceOutbound {
    void ask(byte[] audio, String userId, String channelId);
}
