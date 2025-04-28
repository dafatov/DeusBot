package ru.demetrious.deus.bot.adapter.output.deus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.deus.dto.DeusContext;
import ru.demetrious.deus.bot.app.api.voice.AskByVoiceOutbound;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeusAdapter implements AskByVoiceOutbound {
    private final DeusClient deusClient;

    @Value("${APP_URL}")
    private String appUrl;

    @Override
    public void ask(byte[] audio, String userId, String channelId, String callback) {
        deusClient.askByVoice(new DeusContext(audio, userId, join(EMPTY, appUrl, callback), channelId));
    }
}
