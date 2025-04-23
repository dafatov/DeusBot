package ru.demetrious.deus.bot.app.api.voice;

import java.util.Optional;

public interface StopVoiceRecordOutbound {
    Optional<byte[]> stop(String userId);
}
