package ru.demetrious.deus.bot.app.api.interaction;

@FunctionalInterface
public interface SlashCommandInteractionInbound extends Interaction {
    void execute();
}
