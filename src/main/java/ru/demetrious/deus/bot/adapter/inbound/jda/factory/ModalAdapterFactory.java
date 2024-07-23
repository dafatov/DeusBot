package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.ModalAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.ModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@RequiredArgsConstructor
@Component
public class ModalAdapterFactory {
    private final MessageDataMapper messageDataMapper;

    public ModalAdapter create(ModalInteractionEvent event) {
        return new ModalAdapterImpl(messageDataMapper, event);
    }
}
