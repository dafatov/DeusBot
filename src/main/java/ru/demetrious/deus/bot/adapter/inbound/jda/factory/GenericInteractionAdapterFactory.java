package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@RequiredArgsConstructor
public abstract class GenericInteractionAdapterFactory<Adapter extends GenericInteractionAdapter, Event extends GenericInteractionCreateEvent> {
    protected final MessageDataMapper messageDataMapper;

    public abstract Adapter create(Event event);
}
