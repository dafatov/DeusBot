package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.CommandAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@RequiredArgsConstructor
@Component
public class CommandAdapterFactory {
    private final MessageDataMapper messageDataMapper;

    public CommandAdapter create(SlashCommandInteractionEvent event) {
        return new CommandAdapterImpl(messageDataMapper, event);
    }
}
