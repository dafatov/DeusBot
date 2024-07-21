package ru.demetrious.deus.bot.app.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.domain.CommandData;

@Slf4j
@RequiredArgsConstructor
@Component
public class QueueCommand implements Command {
    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("queue")
            .setDescription("Ну типа выдает очередь. Пока в логи");
    }

    @Override
    public void execute(CommandAdapter commandAdapter) {
        log.debug("queue: {}", commandAdapter.getQueue());
    }
}
