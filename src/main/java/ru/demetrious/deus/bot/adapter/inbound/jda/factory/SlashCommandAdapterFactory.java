package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.SlashCommandAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.ModalDataMapper;

@Component
public class SlashCommandAdapterFactory extends GenericInteractionAdapterFactory<SlashCommandAdapter, SlashCommandInteractionEvent> {
    private final ModalDataMapper modalDataMapper;

    public SlashCommandAdapterFactory(MessageDataMapper messageDataMapper, ModalDataMapper modalDataMapper) {
        super(messageDataMapper);
        this.modalDataMapper = modalDataMapper;
    }

    public SlashCommandAdapter create(SlashCommandInteractionEvent event) {
        return new SlashCommandAdapterImpl(messageDataMapper, event, modalDataMapper);
    }
}
