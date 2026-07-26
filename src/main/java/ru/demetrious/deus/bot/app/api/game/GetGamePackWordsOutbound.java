package ru.demetrious.deus.bot.app.api.game;

import java.util.Set;

@FunctionalInterface
public interface GetGamePackWordsOutbound {
    Set<String> getWords(Long id);
}
