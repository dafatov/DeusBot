package ru.demetrious.deus.bot.app.api.thread;

import java.util.Optional;
import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface CreateThreadOutbound<A extends Interaction> extends HasEventOutbound {
    Optional<String> createThread(String threadName);
}
