package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.OnModalAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.OnModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@RequiredArgsConstructor
@Component
public class OnModalAdapterFactory {
    private final MessageDataMapper messageDataMapper;

    public OnModalAdapter create(ModalInteractionEvent event) {
        return new OnModalAdapterImpl(messageDataMapper, event);
    }
}
