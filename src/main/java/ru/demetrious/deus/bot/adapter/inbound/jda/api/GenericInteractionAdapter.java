package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import ru.demetrious.deus.bot.adapter.inbound.jda.handler.AudioSendHandlerImpl;
import ru.demetrious.deus.bot.domain.MessageData;

public interface GenericInteractionAdapter {
    void notify(MessageData messageData);

    String getGuildId();

    boolean isNotConnectedSameChannel();

    boolean isNotCanConnect();

    String getAuthorId();

    void connectPlayer(AudioSendHandlerImpl audioSendHandler);
}
