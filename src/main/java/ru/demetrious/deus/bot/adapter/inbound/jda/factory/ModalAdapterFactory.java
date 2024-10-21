package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.ModalAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.ModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@Component
public class ModalAdapterFactory extends GenericInteractionAdapterFactory<ModalAdapter, ModalInteractionEvent> {
    public ModalAdapterFactory(MessageDataMapper messageDataMapper) {
        super(messageDataMapper);
    }

    public ModalAdapter create(ModalInteractionEvent event) {
        return new ModalAdapterImpl(messageDataMapper, event);
    }
}
