package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.ButtonAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.ButtonAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@Component
public class ButtonAdapterFactory extends GenericInteractionAdapterFactory<ButtonAdapter, ButtonInteractionEvent> {
    public ButtonAdapterFactory(MessageDataMapper messageDataMapper) {
        super(messageDataMapper);
    }

    @Override
    public ButtonAdapter create(ButtonInteractionEvent event) {
        return new ButtonAdapterImpl(event, messageDataMapper);
    }
}
