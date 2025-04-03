package ru.demetrious.deus.bot.app.api.thread;

import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface LeaveThreadOutbound<A extends Interaction> extends HasEventOutbound {
    void leaveThread(String id);
}
