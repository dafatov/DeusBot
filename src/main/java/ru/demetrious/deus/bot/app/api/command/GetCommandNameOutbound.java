package ru.demetrious.deus.bot.app.api.command;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;
import ru.demetrious.deus.bot.domain.CommandData.Name;

public interface GetCommandNameOutbound<A extends Interaction> extends HasEventOutbound {
    Name getCommandName();
}
