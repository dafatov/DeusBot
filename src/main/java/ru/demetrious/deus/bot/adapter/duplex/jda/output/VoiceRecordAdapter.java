package ru.demetrious.deus.bot.adapter.duplex.jda.output;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.AudioInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.voice.StartVoiceRecordOutbound;
import ru.demetrious.deus.bot.app.api.voice.StopVoiceRecordOutbound;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static javax.sound.sampled.AudioFileFormat.Type.WAVE;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioSystem.write;
import static net.dv8tion.jda.api.audio.AudioReceiveHandler.OUTPUT_FORMAT;
import static org.apache.commons.lang3.ArrayUtils.addAll;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoiceRecordAdapter implements StartVoiceRecordOutbound, StopVoiceRecordOutbound {
    private final static Map<String, byte[]> USER_AUDIO = new ConcurrentHashMap<>();

    public void onUserAudioSample(String userId, byte[] audioSample) {
        USER_AUDIO.computeIfPresent(userId, (s, bytes) -> addAll(bytes, audioSample));
    }

    @Override
    public boolean start(String userId) {
        if (USER_AUDIO.containsKey(userId)) {
            return false;
        }

        USER_AUDIO.put(userId, new byte[0]);
        return true;
    }

    @Override
    public Optional<byte[]> stop(String userId) {
        byte[] rawAudio = USER_AUDIO.remove(userId);

        if (rawAudio == null) {
            return empty();
        }

        ByteArrayInputStream rawByteArrayInputStream = new ByteArrayInputStream(rawAudio);
        AudioInputStream rawAudioInputStream = new AudioInputStream(rawByteArrayInputStream, OUTPUT_FORMAT, rawAudio.length);
        AudioInputStream audioInputStream = getAudioInputStream(PCM_SIGNED, rawAudioInputStream);
        ByteArrayOutputStream waveByteArrayOutputStream = new ByteArrayOutputStream();

        try {
            write(audioInputStream, WAVE, waveByteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return of(waveByteArrayOutputStream.toByteArray());
    }
}
