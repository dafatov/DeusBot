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
    //private final static ConcurrentHashMap<String, byte[]> audioMap2 = new ConcurrentHashMap<>();

    private final VoiceRecordAdapter voiceRecordAdapter;

    @Override
    public boolean canReceiveUser() {
        return true;
    }

    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {
        voiceRecordAdapter.onUserAudioSample(userAudio.getUser().getId(), userAudio.getAudioData(1.));
    }

        /*byte[] computed = audioMap2.compute(id, (s, bytes) -> {
            if (bytes == null) {
                return audioData;
            }

            return addAll(bytes, audioData);
        });*/

        /*try {
            if (computed.length > 9000000) {
                AudioInputStream audioInputStream = getAudioInputStream(PCM_SIGNED, new AudioInputStream(new ByteArrayInputStream(computed), OUTPUT_FORMAT, computed.length));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                AudioSystem.write(audioInputStream, WAVE, byteArrayOutputStream);

                publishMessageOutbound.publish("909872119695966228", new MessageData().setContent("<@%s>".formatted(id))
                    .setFiles(List.of(new MessageFile().setName("audio_%s.wav".formatted(id)).setData(byteArrayOutputStream.toByteArray()))));
                audioMap2.remove(id);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    //}
}
