package ru.demetrious.deus.bot.app.api.message;

import ru.demetrious.deus.bot.domain.MessageData;

@FunctionalInterface
public interface UpdateMessageOutbound {
    void update(MessageData messageData);
}
