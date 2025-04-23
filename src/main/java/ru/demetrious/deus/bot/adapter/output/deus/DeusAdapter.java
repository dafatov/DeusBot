package ru.demetrious.deus.bot.adapter.output.deus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.voice.AskByVoiceOutbound;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeusAdapter implements AskByVoiceOutbound {
    private final DeusClient deusClient;

    @Override
    public String ask(byte[] audio, String userId) {
        return deusClient.askByVoice(audio, userId);
    }
}
