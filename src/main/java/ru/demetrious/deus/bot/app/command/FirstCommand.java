package ru.demetrious.deus.bot.app.command;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.util.Optional.of;
import static ru.demetrious.deus.bot.app.command.MoveCommand.TARGET_DESCRIPTION;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;

@Slf4j
@Component
public class FirstCommand extends PlayerCommand {
    private final MoveCommand moveCommand;

    public FirstCommand(Jukebox jukebox, MoveCommand moveCommand) {
        super(jukebox);
        this.moveCommand = moveCommand;
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("first")
            .setDescription("Переместить композицию с места в очереди на первую")
            .setOptions(List.of(
                new OptionData()
                    .setType(INTEGER)
                    .setName("target")
                    .setDescription(TARGET_DESCRIPTION)
                    .setRequired(true)
            ));
    }

    @Override
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        moveCommand.move(slashCommandAdapter, slashCommandAdapter.getIntegerOption("target").map(index -> index - 1), of(0));
    }
}
