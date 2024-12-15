package ru.demetrious.deus.bot.app.api.interaction;

@FunctionalInterface
public interface ButtonInteractionInbound extends Interaction {
    void onButton();
}
