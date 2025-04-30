package ru.demetrious.deus.bot.app.impl.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.message.PublishMessageInbound;
import ru.demetrious.deus.bot.app.api.message.PublishMessageOutbound;
import ru.demetrious.deus.bot.domain.MessageData;

@RequiredArgsConstructor
@Component
public class PublishMessageUseCase implements PublishMessageInbound {
    private final PublishMessageOutbound publishMessageOutbound;

    @Override
    public void publish(String channelId, MessageData messageData) {
        publishMessageOutbound.publish(channelId, messageData);
    }
}
