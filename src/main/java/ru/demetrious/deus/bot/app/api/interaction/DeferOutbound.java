package ru.demetrious.deus.bot.app.api.interaction;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;

public interface DeferOutbound<A extends Interaction> extends HasEventOutbound {
    void defer();
}
