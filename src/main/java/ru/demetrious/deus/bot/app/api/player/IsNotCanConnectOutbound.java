package ru.demetrious.deus.bot.app.api.player;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface IsNotCanConnectOutbound<A extends Interaction> extends HasEventOutbound {
    boolean isNotCanConnect();
}
