package ru.demetrious.deus.bot.app.command.api;

import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.OnModalAdapter;
import ru.demetrious.deus.bot.domain.CommandData;

public interface Command {
    CommandData getData();

    void execute(CommandAdapter commandAdapter);

    default boolean isDeferReply(CommandAdapter commandAdapter) {
        return true;
    }

    default void onModal(OnModalAdapter onModalAdapter) {
        throw new IllegalStateException("Command onModal is not implemented");
    }
}
