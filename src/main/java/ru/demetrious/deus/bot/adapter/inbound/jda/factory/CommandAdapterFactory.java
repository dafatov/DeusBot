package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.demetrious.deus.bot.adapter.inbound.jda.CommandAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapperImpl;

// TODO: гигантский костыль, который я уверен не обязателен и можно сделать по уму
public class CommandAdapterFactory {
    public static CommandAdapter create(SlashCommandInteractionEvent event) {
        return new CommandAdapterImpl(new MessageDataMapperImpl(), event);
    }
}
