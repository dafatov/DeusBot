package ru.demetrious.deus.bot.app.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;

@Slf4j
@Component
public class QueueCommand extends PlayerCommand {
    public QueueCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("queue")
            .setDescription("Ну типа выдает очередь. Пока в логи");
    }

    @Override
    public void execute(CommandAdapter commandAdapter) {
        final Player player = getPlayer(commandAdapter);

        log.debug("queue: {}", player.getQueue());
    }
}
