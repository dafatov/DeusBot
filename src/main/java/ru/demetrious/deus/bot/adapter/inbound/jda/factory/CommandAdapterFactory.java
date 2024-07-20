package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.demetrious.deus.bot.adapter.inbound.jda.CommandAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapperImpl;
import ru.demetrious.deus.bot.app.player.JukeboxImpl;
import ru.demetrious.deus.bot.app.player.api.Jukebox;

import static java.util.Optional.ofNullable;

// TODO: гигантский костыль, который я уверен не обязателен и можно сделать по уму
public class CommandAdapterFactory {
    private static final Jukebox JUKEBOX = new JukeboxImpl();
    private static final MessageDataMapper MESSAGE_DATA_MAPPER = new MessageDataMapperImpl();

    public static CommandAdapter create(SlashCommandInteractionEvent event) {
        return new CommandAdapterImpl(MESSAGE_DATA_MAPPER, event, JUKEBOX.getPlayer(ofNullable(event.getGuild()).map(ISnowflake::getId).orElseThrow()));
    }
}
