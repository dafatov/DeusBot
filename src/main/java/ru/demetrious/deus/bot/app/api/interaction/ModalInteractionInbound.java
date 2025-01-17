package ru.demetrious.deus.bot.app.api.interaction;

@FunctionalInterface
public interface ModalInteractionInbound extends Interaction {
    void onModal();
}
