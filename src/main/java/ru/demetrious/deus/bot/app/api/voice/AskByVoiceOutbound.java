package ru.demetrious.deus.bot.app.api.voice;

public interface AskByVoiceOutbound {
    String ask(byte[] audio, String userId);
}
