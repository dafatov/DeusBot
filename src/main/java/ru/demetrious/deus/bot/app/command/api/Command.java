package ru.demetrious.deus.bot.app.command.api;

import ru.demetrious.deus.bot.adapter.inbound.jda.api.ModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.domain.CommandData;

public interface Command {
    CommandData getData();

    void execute(SlashCommandAdapter slashCommandAdapter);

    default boolean isDeferReply(SlashCommandAdapter slashCommandAdapter) {
        return true;
    }

    default void onModal(ModalAdapter modalAdapter) {
        throw new IllegalStateException("Command onModal is not implemented");
    }
}
