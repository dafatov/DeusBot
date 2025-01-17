package ru.demetrious.deus.bot.app.api.interaction;

@FunctionalInterface
public interface AutocompleteInteractionInbound extends Interaction {
    void onAutocomplete();
}
