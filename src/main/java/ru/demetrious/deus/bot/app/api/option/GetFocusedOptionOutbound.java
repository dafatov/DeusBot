package ru.demetrious.deus.bot.app.api.option;

import ru.demetrious.deus.bot.domain.AutocompleteOption;

@FunctionalInterface
public interface GetFocusedOptionOutbound {
    AutocompleteOption getFocusedOption();
}
