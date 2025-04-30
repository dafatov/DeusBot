package ru.demetrious.deus.bot.adapter.duplex.jda.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.VoiceRecordAdapter;

@Slf4j
@RequiredArgsConstructor
@Component
public class AudioReceiveHandler implements net.dv8tion.jda.api.audio.AudioReceiveHandler {
    private final VoiceRecordAdapter voiceRecordAdapter;

    @Override
    public boolean canReceiveUser() {
        return true;
    }

    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {
        voiceRecordAdapter.onUserAudioSample(userAudio.getUser().getId(), userAudio.getAudioData(1.));
    }
}
