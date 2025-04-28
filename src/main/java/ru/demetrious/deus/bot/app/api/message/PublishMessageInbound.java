package ru.demetrious.deus.bot.app.api.message;

import ru.demetrious.deus.bot.domain.MessageData;

@FunctionalInterface
public interface PublishMessageInbound {
    String MESSAGE_PATH = "/api/message";
    String PUBLISH_PATH = "/publish";

    void publish(String channelId, MessageData messageData);
}
