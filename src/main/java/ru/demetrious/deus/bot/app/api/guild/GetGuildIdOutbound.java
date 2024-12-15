package ru.demetrious.deus.bot.app.api.guild;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface GetGuildIdOutbound<A extends Interaction> extends HasEventOutbound {
    String getGuildId();
}
