package ru.demetrious.deus.bot.app.api.user;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface GetUserIdOutbound<A extends Interaction> extends HasEventOutbound {
    String getUserId();
}
