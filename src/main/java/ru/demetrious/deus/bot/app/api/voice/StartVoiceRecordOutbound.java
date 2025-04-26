package ru.demetrious.deus.bot.app.api.voice;

@FunctionalInterface
public interface StartVoiceRecordOutbound {
    boolean start(String userId);
}
