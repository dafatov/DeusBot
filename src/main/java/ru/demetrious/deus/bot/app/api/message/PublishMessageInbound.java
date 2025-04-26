package ru.demetrious.deus.bot.app.api.message;

import ru.demetrious.deus.bot.domain.MessageData;

@FunctionalInterface
public interface PublishMessageInbound {
    void publish(String channelId, MessageData messageData);
}
