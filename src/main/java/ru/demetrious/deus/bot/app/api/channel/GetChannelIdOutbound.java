package ru.demetrious.deus.bot.app.api.channel;

import java.util.Optional;
import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface GetChannelIdOutbound<A extends Interaction> extends HasEventOutbound {
    Optional<String> getChannelId();
}
