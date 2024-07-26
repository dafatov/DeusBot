package ru.demetrious.deus.bot.adapter.inbound.jda;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.ButtonAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

public class ButtonAdapterImpl extends GenericInteractionAdapterImpl<ButtonInteractionEvent, ButtonInteraction> implements ButtonAdapter {
    public ButtonAdapterImpl(ButtonInteractionEvent event, MessageDataMapper messageDataMapper) {
        super(event, messageDataMapper);
    }

    @Override
    public MessageEmbed getEmbed(int index) {
        return messageDataMapper.mapEmbed(event.getMessage().getEmbeds().get(index));
    }

    @Override
    public void update(MessageData messageData) {
        event.editMessage(messageDataMapper.mapToMessageEdit(messageData)).queue();
    }

    public String getCustomId() {
        return event.getComponentId();
    }
}
