package ru.demetrious.deus.bot.app.api.character;

import java.util.Map;
import ru.demetrious.deus.bot.domain.Character;

@FunctionalInterface
public interface GetReverseCharacterListOutbound {
    Map<Integer, Character> getReverseCharacterList();
}
