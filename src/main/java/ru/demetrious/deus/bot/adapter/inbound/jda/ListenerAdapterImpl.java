package ru.demetrious.deus.bot.adapter.inbound.jda;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.command.api.Command;

import static ru.demetrious.deus.bot.adapter.inbound.jda.factory.CommandAdapterFactory.create;

@RequiredArgsConstructor
@Component
public class ListenerAdapterImpl extends ListenerAdapter {
    private final List<Command> commandList;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        commandList.stream()
            .filter(command -> command.getData().getName().equals(event.getName()))
            .findFirst()
            .orElseThrow()
            .execute(create(event));
    }
}
