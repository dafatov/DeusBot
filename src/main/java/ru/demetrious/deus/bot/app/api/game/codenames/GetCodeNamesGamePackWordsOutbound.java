package ru.demetrious.deus.bot.app.api.game.codenames;

import java.util.Set;

@FunctionalInterface
public interface GetCodeNamesGamePackWordsOutbound {
    Set<String> getWords(Long id);
}
