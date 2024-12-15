package ru.demetrious.deus.bot.app.api.user;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface GetAuthorIdOutbound<A extends Interaction> extends HasEventOutbound {
    String getAuthorId();
}
