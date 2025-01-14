package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.app.api.command.GetCommandNameOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;

import static java.util.Objects.nonNull;

public abstract class BaseAdapter<E, A extends Interaction> implements GetCommandNameOutbound<A>, GetUserIdOutbound<A> {
    private final ThreadLocal<E> event = new ThreadLocal<>();

    public void removeEvent() {
        this.event.remove();
    }

    protected E getEvent() {
        return event.get();
    }

    public void setEvent(@NotNull E event) {
        this.event.set(event);
    }

    @Override
    public boolean hasEvent() {
        return nonNull(getEvent());
    }
}
