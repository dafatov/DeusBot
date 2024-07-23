package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.SlashCommandAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@RequiredArgsConstructor
@Component
public class SlashCommandAdapterFactory {
    private final MessageDataMapper messageDataMapper;

    public SlashCommandAdapter create(SlashCommandInteractionEvent event) {
        return new SlashCommandAdapterImpl(messageDataMapper, event);
    }
}
