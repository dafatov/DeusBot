package ru.demetrious.deus.bot.app.command.api;

import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.domain.CommandData;

public interface Command {
    CommandData getData();

    void execute(CommandAdapter commandAdapter);
}
