package ru.demetrious.deus.bot.app.api.message;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;
import ru.demetrious.deus.bot.domain.MessageData;

public interface NotifyOutbound<I extends Interaction> extends HasEventOutbound {
    void notify(MessageData messageData, boolean isEphemeral);

    void notify(MessageData messageData);
}
