package ru.demetrious.deus.bot.app.api.voice;

import java.util.Optional;

@FunctionalInterface
public interface StopVoiceRecordOutbound {
    Optional<byte[]> stop(String userId);
}
