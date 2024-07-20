package ru.demetrious.deus.bot.app.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.domain.CommandData;

@Slf4j
@Component
public class PlayCommand implements Command {
    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("play")
            .setDescription("Ну типа play");
    }

    @Override
    public void execute(CommandAdapter commandAdapter) {
        commandAdapter.connectPlayer();
    }
}
