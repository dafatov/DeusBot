package ru.demetrious.deus.bot.adapter.inbound.jda.factory;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.CommandAdapterImpl;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.app.player.api.Jukebox;

import static java.util.Optional.ofNullable;

// TODO: костыль?
@RequiredArgsConstructor
@Component
public class CommandAdapterFactory {
    private final Jukebox jukebox;
    private final MessageDataMapper messageDataMapper;

    public CommandAdapter create(SlashCommandInteractionEvent event) {
        return new CommandAdapterImpl(messageDataMapper, event, jukebox.getPlayer(ofNullable(event.getGuild()).map(ISnowflake::getId).orElseThrow()));
    }
}
